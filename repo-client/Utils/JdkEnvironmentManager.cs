using System;
using System.IO;
using System.IO.Compression;
using System.Diagnostics;
using SevenZipExtractor;

namespace EasyYachiyoClient.Utils
{
    /// <summary>
    /// JDK环境管理类，负责JDK环境的验证、解压和配置
    /// </summary>
    public static class JdkEnvironmentManager
    {
        /// <summary>
        /// JDK压缩文件路径
        /// </summary>
        private static string JdkArchivePath => Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "resource", "jdk", "jdk-25.7z");

        /// <summary>
        /// JDK解压目录
        /// </summary>
        private static string JdkExtractPath => Path.Combine(Path.GetTempPath(), "EasyYachiyoClient", "jdk-25");

        /// <summary>
        /// 验证JDK压缩文件的完整性和可用性
        /// </summary>
        /// <returns>JDK压缩文件是否可用</returns>
        public static bool ValidateJdkArchive()
        {
            try
            {
                LogManager.Info($"验证JDK压缩文件: {JdkArchivePath}");
                
                // 检查文件是否存在
                if (!File.Exists(JdkArchivePath))
                {
                    LogManager.Error("JDK压缩文件不存在");
                    return false;
                }
                
                // 检查文件大小（至少应该有一定大小）
                FileInfo fileInfo = new FileInfo(JdkArchivePath);
                if (fileInfo.Length < 1024 * 1024) // 至少1MB
                {
                    LogManager.Error("JDK压缩文件大小异常");
                    return false;
                }
                
                LogManager.Info($"JDK压缩文件验证成功，大小: {fileInfo.Length / (1024 * 1024)} MB");
                return true;
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "JdkEnvironmentManager.ValidateJdkArchive");
                ApplicationStateManager.LogException(ex, "JdkEnvironmentManager.ValidateJdkArchive");
                return false;
            }
        }

        /// <summary>
        /// 进度报告委托
        /// </summary>
        /// <param name="progress">进度值（0-100）</param>
        /// <param name="message">进度消息</param>
        public delegate void ProgressReportDelegate(int progress, string message);

        /// <summary>
        /// 解压JDK压缩文件
        /// </summary>
        /// <param name="progressReport">进度报告回调</param>
        /// <returns>解压是否成功</returns>
        public static bool ExtractJdk(ProgressReportDelegate progressReport = null)
        {
            try
            {
                LogManager.Info("开始解压JDK压缩文件");
                
                // 检查系统临时文件夹中是否已存在JDK目录且完整
                LogManager.Info($"JdkExtractPath: {JdkExtractPath}");
                LogManager.Info($"检查JDK目录是否存在: {JdkExtractPath}");
                if (Directory.Exists(JdkExtractPath))
                {
                    LogManager.Info("JDK目录已存在，检查是否完整");
                    bool jdkStructureValid = ValidateJdkStructure();
                    if (jdkStructureValid)
                    {
                        LogManager.Info("JDK目录已存在且完整，直接使用");
                        return true;
                    }
                    else
                    {
                        LogManager.Info("JDK目录已存在但不完整，需要重新解压");
                    }
                }
                else
                {
                    LogManager.Info("JDK目录不存在，需要解压");
                }
                
                // 创建临时目录
                string tempDir = Path.GetDirectoryName(JdkExtractPath);
                LogManager.Info($"创建临时目录: {tempDir}");
                if (!Directory.Exists(tempDir))
                {
                    try
                    {
                        Directory.CreateDirectory(tempDir);
                        LogManager.Info($"临时目录创建成功: {tempDir}");
                    }
                    catch (Exception ex)
                    {
                        LogManager.Error($"创建临时目录失败: {ex.Message}");
                        LogManager.Error($"错误类型: {ex.GetType().Name}");
                        if (ex is UnauthorizedAccessException)
                        {
                            LogManager.Error("无权限创建临时目录，请以管理员身份运行应用程序");
                        }
                        else if (ex is PathTooLongException)
                        {
                            LogManager.Error("临时目录路径过长，请检查系统临时文件夹设置");
                        }
                        return false;
                    }
                }
                else
                {
                    LogManager.Info($"临时目录已存在: {tempDir}");
                }
                
                // 删除已存在的解压目录
                if (Directory.Exists(JdkExtractPath))
                {
                    LogManager.Info($"删除已存在的解压目录: {JdkExtractPath}");
                    try
                    {
                        Directory.Delete(JdkExtractPath, true);
                        LogManager.Info($"已删除解压目录: {JdkExtractPath}");
                    }
                    catch (Exception ex)
                    {
                        LogManager.Error($"删除解压目录失败: {ex.Message}");
                        LogManager.Error($"错误类型: {ex.GetType().Name}");
                        if (ex is UnauthorizedAccessException)
                        {
                            LogManager.Error("无权限删除解压目录，请关闭占用该目录的应用程序");
                        }
                        else if (ex is IOException)
                        {
                            LogManager.Error("解压目录正在被使用，请关闭占用该目录的应用程序");
                        }
                        return false;
                    }
                }
                
                // 使用SevenZipExtractor解压7z文件
                LogManager.Info("使用SevenZipExtractor库解压JDK文件");
                bool extractSuccess = ExtractUsingSevenZipExtractor(progressReport);
                
                // 如果使用SevenZipExtractor解压失败，尝试其他方法
                if (!extractSuccess)
                {
                    LogManager.Warn("SevenZipExtractor解压失败，尝试使用其他方法");
                    extractSuccess = ExtractUsing7Zip();
                }
                
                if (extractSuccess)
                {
                    // 验证JDK文件结构完整性
                    LogManager.Info("验证JDK文件结构完整性");
                    bool structureValid = ValidateJdkStructure();
                    if (structureValid)
                    {
                        LogManager.Info($"JDK解压成功且文件结构完整: {JdkExtractPath}");
                        return true;
                    }
                    else
                    {
                        LogManager.Error("JDK解压成功但文件结构不完整");
                        LogManager.Error("可能的原因: 解压过程中被中断、磁盘空间不足、文件损坏");
                        return false;
                    }
                }
                else
                {
                    LogManager.Error("JDK解压失败");
                    LogManager.Error("可能的原因: 压缩文件损坏、无权限访问、磁盘空间不足、7-Zip未安装");
                    return false;
                }
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "JdkEnvironmentManager.ExtractJdk");
                LogManager.Error($"解压过程中发生未预期的错误: {ex.Message}");
                LogManager.Error($"错误类型: {ex.GetType().Name}");
                ApplicationStateManager.LogException(ex, "JdkEnvironmentManager.ExtractJdk");
                return false;
            }
        }

        /// <summary>
        /// 使用SevenZipExtractor库解压7z文件
        /// </summary>
        /// <param name="progressReport">进度报告回调</param>
        /// <returns>解压是否成功</returns>
        private static bool ExtractUsingSevenZipExtractor(ProgressReportDelegate progressReport = null)
        {
            try
            {
                LogManager.Info("使用SevenZipExtractor解压JDK文件");
                LogManager.Info($"JDK压缩文件路径: {JdkArchivePath}");
                LogManager.Info($"JDK解压目录路径: {JdkExtractPath}");
                
                // 检查压缩文件是否存在
                if (!File.Exists(JdkArchivePath))
                {
                    LogManager.Error($"JDK压缩文件不存在: {JdkArchivePath}");
                    return false;
                }
                else
                {
                    LogManager.Info($"JDK压缩文件存在: {JdkArchivePath}");
                }
                
                // 检查压缩文件权限
                try
                {
                    using (FileStream fs = File.OpenRead(JdkArchivePath))
                    {
                        // 测试文件是否可读
                        LogManager.Info("JDK压缩文件可读");
                    }
                }
                catch (UnauthorizedAccessException ex)
                {
                    LogManager.Error($"无权限读取JDK压缩文件: {ex.Message}");
                    return false;
                }
                
                // 创建解压目录
                if (!Directory.Exists(JdkExtractPath))
                {
                    try
                    {
                        Directory.CreateDirectory(JdkExtractPath);
                        LogManager.Info($"创建解压目录成功: {JdkExtractPath}");
                    }
                    catch (Exception ex)
                    {
                        LogManager.Error($"创建解压目录失败: {ex.Message}");
                        return false;
                    }
                }
                else
                {
                    LogManager.Info($"解压目录已存在: {JdkExtractPath}");
                }
                
                // 检查解压目录权限
                try
                {
                    string testFile = Path.Combine(JdkExtractPath, "test.tmp");
                    File.WriteAllText(testFile, "test");
                    File.Delete(testFile);
                    LogManager.Info("解压目录可写");
                }
                catch (UnauthorizedAccessException ex)
                {
                    LogManager.Error($"无权限写入解压目录: {ex.Message}");
                    return false;
                }
                
                // 使用SevenZipExtractor解压
                try
                {
                    LogManager.Info("开始打开压缩文件");
                    using (ArchiveFile archive = new ArchiveFile(JdkArchivePath))
                    {
                        LogManager.Info($"打开压缩文件成功，包含 {archive.Entries.Count} 个文件");
                        
                        // 解压所有文件
                        int extractedCount = 0;
                        int failedCount = 0;
                        int totalEntries = archive.Entries.Count;
                        foreach (var entry in archive.Entries)
                        {
                            try
                            {
                                string entryPath = Path.Combine(JdkExtractPath, entry.FileName);
                                string entryDir = Path.GetDirectoryName(entryPath);
                                
                                // 创建目录
                                if (!Directory.Exists(entryDir))
                                {
                                    Directory.CreateDirectory(entryDir);
                                }
                                
                                // 解压文件
                                entry.Extract(entryPath);
                                extractedCount++;
                                
                                // 计算进度并报告
                                int progress = (int)((float)extractedCount / totalEntries * 100);
                                string message = $"正在解压: {Path.GetFileName(entry.FileName)}";
                                progressReport?.Invoke(progress, message);
                                
                                // 每解压10个文件记录一次日志
                                if (extractedCount % 10 == 0)
                                {
                                    LogManager.Info($"已解压 {extractedCount}/{totalEntries} 个文件");
                                }
                            }
                            catch (Exception ex)
                            {
                                LogManager.Warn($"解压文件 {entry.FileName} 失败: {ex.Message}");
                                failedCount++;
                            }
                        }
                        
                        LogManager.Info($"解压完成，共解压 {extractedCount}/{totalEntries} 个文件，失败 {failedCount} 个");
                        
                        // 检查是否有文件被解压
                        if (extractedCount == 0)
                        {
                            LogManager.Error("没有文件被成功解压");
                            return false;
                        }
                    }
                }
                catch (Exception ex)
                {
                    LogManager.Error($"使用SevenZipExtractor解压失败: {ex.Message}");
                    return false;
                }
                
                // 验证解压结果
                string javaExePath = FindJavaExe();
                if (javaExePath == null)
                {
                    LogManager.Error("解压后找不到java.exe，解压可能不完整");
                    return false;
                }
                else
                {
                    LogManager.Info($"解压后找到java.exe: {javaExePath}");
                }
                
                LogManager.Info("SevenZipExtractor解压成功");
                return true;
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "JdkEnvironmentManager.ExtractUsingSevenZipExtractor");
                return false;
            }
        }

        /// <summary>
        /// 使用7-Zip命令行工具解压
        /// </summary>
        /// <returns>解压是否成功</returns>
        private static bool ExtractUsing7Zip()
        {
            try
            {
                // 检查系统是否安装了7-Zip
                string sevenZipPath = Get7ZipPath();
                if (string.IsNullOrEmpty(sevenZipPath))
                {
                    LogManager.Warn("系统未安装7-Zip，尝试使用其他方法解压");
                    // 如果没有7-Zip，尝试使用其他方法
                    return ExtractUsingAlternativeMethod();
                }
                
                LogManager.Info("使用7-Zip解压JDK文件");
                
                // 构建解压命令
                string arguments = $"x -o{JdkExtractPath} {JdkArchivePath} -y";
                
                using Process process = new Process
                {
                    StartInfo = new ProcessStartInfo
                    {
                        FileName = sevenZipPath,
                        Arguments = arguments,
                        RedirectStandardOutput = true,
                        RedirectStandardError = true,
                        UseShellExecute = false,
                        CreateNoWindow = true
                    }
                };
                
                process.Start();
                string output = process.StandardOutput.ReadToEnd();
                string error = process.StandardError.ReadToEnd();
                process.WaitForExit();
                
                if (process.ExitCode == 0)
                {
                    LogManager.Info("7-Zip解压成功");
                    return true;
                }
                else
                {
                    LogManager.Error($"7-Zip解压失败: {error}");
                    return false;
                }
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "JdkEnvironmentManager.ExtractUsing7Zip");
                return false;
            }
        }

        /// <summary>
        /// 获取7-Zip可执行文件路径
        /// </summary>
        /// <returns>7-Zip可执行文件路径</returns>
        private static string Get7ZipPath()
        {
            // 检查常见的7-Zip安装路径
            string[] possiblePaths = {
                Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ProgramFiles), "7-Zip", "7z.exe"),
                Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ProgramFilesX86), "7-Zip", "7z.exe")
            };
            
            foreach (string path in possiblePaths)
            {
                if (File.Exists(path))
                {
                    return path;
                }
            }
            
            return null;
        }

        /// <summary>
        /// 使用替代方法解压（如果没有7-Zip）
        /// </summary>
        /// <returns>解压是否成功</returns>
        private static bool ExtractUsingAlternativeMethod()
        {
            try
            {
                LogManager.Info("尝试使用替代方法解压JDK文件");
                
                // 检查文件扩展名，确定文件格式
                string extension = Path.GetExtension(JdkArchivePath).ToLower();
                
                if (extension == ".zip")
                {
                    // 如果是zip格式，使用内置的ZipArchive
                    LogManager.Info("使用内置ZipArchive解压ZIP格式的JDK文件");
                    return ExtractUsingZipArchive();
                }
                else if (extension == ".7z")
                {
                    // 如果是7z格式，检查是否有其他可用的解压工具
                    LogManager.Warn("系统未安装7-Zip，无法解压7z格式的JDK文件");
                    LogManager.Warn("请安装7-Zip或使用ZIP格式的JDK文件");
                    
                    // 尝试使用系统可能存在的其他解压工具
                    if (TryExtractWithPowerShell())
                    {
                        return true;
                    }
                }
                
                // 作为最后的尝试，检查是否已经有解压好的JDK
                if (Directory.Exists(JdkExtractPath) && FindJavaExe() != null)
                {
                    LogManager.Info("发现已解压的JDK目录，跳过解压步骤");
                    return true;
                }
                
                LogManager.Error("无法解压JDK文件，请确保系统已安装7-Zip");
                return false;
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "JdkEnvironmentManager.ExtractUsingAlternativeMethod");
                return false;
            }
        }

        /// <summary>
        /// 使用内置的ZipArchive解压ZIP格式的文件
        /// </summary>
        /// <returns>解压是否成功</returns>
        private static bool ExtractUsingZipArchive()
        {
            try
            {
                LogManager.Info("使用内置ZipArchive解压JDK文件");
                
                using (System.IO.Compression.ZipArchive archive = System.IO.Compression.ZipFile.OpenRead(JdkArchivePath))
                {
                    foreach (System.IO.Compression.ZipArchiveEntry entry in archive.Entries)
                    {
                        string entryPath = Path.Combine(JdkExtractPath, entry.FullName);
                        string entryDir = Path.GetDirectoryName(entryPath);
                        
                        if (!Directory.Exists(entryDir))
                        {
                            Directory.CreateDirectory(entryDir);
                        }
                        
                        if (!string.IsNullOrEmpty(entry.Name))
                        {
                            entry.ExtractToFile(entryPath, true);
                        }
                    }
                }
                
                LogManager.Info("ZipArchive解压成功");
                return true;
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "JdkEnvironmentManager.ExtractUsingZipArchive");
                return false;
            }
        }

        /// <summary>
        /// 尝试使用PowerShell解压文件
        /// </summary>
        /// <returns>解压是否成功</returns>
        private static bool TryExtractWithPowerShell()
        {
            try
            {
                LogManager.Info("尝试使用PowerShell解压JDK文件");
                
                // 构建PowerShell命令
                string script = $@"
                $archivePath = '{JdkArchivePath}';
                $extractPath = '{JdkExtractPath}';
                
                # 检查是否存在System.IO.Compression.FileSystem
                try {{
                    Add-Type -AssemblyName System.IO.Compression.FileSystem -ErrorAction Stop;
                    
                    # 尝试解压
                    if (Test-Path $archivePath) {{
                        if (!(Test-Path $extractPath)) {{
                            New-Item -ItemType Directory -Path $extractPath -Force | Out-Null;
                        }}
                        
                        Write-Host '尝试使用PowerShell解压...';
                        # 注意：PowerShell的Expand-Archive可能不支持7z格式
                        # 这里只是一个尝试
                        try {{
                            Expand-Archive -Path $archivePath -DestinationPath $extractPath -Force;
                            Write-Host '解压成功';
                            exit 0;
                        }} catch {{
                            Write-Host 'Expand-Archive失败: ' + $_.Exception.Message;
                            exit 1;
                        }}
                    }} else {{
                        Write-Host '文件不存在';
                        exit 1;
                    }}
                }} catch {{
                    Write-Host '无法加载System.IO.Compression.FileSystem: ' + $_.Exception.Message;
                    exit 1;
                }}
                ";
                
                using Process process = new Process
                {
                    StartInfo = new ProcessStartInfo
                    {
                        FileName = "powershell.exe",
                        Arguments = $"-ExecutionPolicy Bypass -Command {script}",
                        RedirectStandardOutput = true,
                        RedirectStandardError = true,
                        UseShellExecute = false,
                        CreateNoWindow = true
                    }
                };
                
                process.Start();
                string output = process.StandardOutput.ReadToEnd();
                string error = process.StandardError.ReadToEnd();
                process.WaitForExit();
                
                if (process.ExitCode == 0)
                {
                    LogManager.Info("PowerShell解压成功");
                    return true;
                }
                else
                {
                    LogManager.Warn($"PowerShell解压失败: {error}");
                    return false;
                }
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "JdkEnvironmentManager.TryExtractWithPowerShell");
                return false;
            }
        }

        /// <summary>
        /// 配置JDK环境变量
        /// </summary>
        /// <returns>环境变量配置是否成功</returns>
        public static bool ConfigureJdkEnvironment()
        {
            try
            {
                LogManager.Info("配置JDK环境变量");
                
                // 确保JDK已经解压
                if (!Directory.Exists(JdkExtractPath))
                {
                    LogManager.Error("JDK未解压");
                    LogManager.Error("请先执行ExtractJdk方法解压JDK");
                    return false;
                }
                
                // 查找java.exe路径
                string javaExePath = FindJavaExe();
                if (string.IsNullOrEmpty(javaExePath))
                {
                    LogManager.Error("找不到java.exe");
                    LogManager.Error("可能的原因: JDK解压不完整、文件损坏、路径配置错误");
                    return false;
                }
                
                LogManager.Info($"找到java.exe: {javaExePath}");
                
                // 检查java.exe是否可执行
                if (!File.Exists(javaExePath))
                {
                    LogManager.Error($"java.exe文件不存在: {javaExePath}");
                    return false;
                }
                
                try
                {
                    // 配置PATH环境变量（临时，仅在当前进程中有效）
                    string currentPath = Environment.GetEnvironmentVariable("PATH", EnvironmentVariableTarget.Process);
                    string jdkBinPath = Path.GetDirectoryName(javaExePath);
                    
                    if (!currentPath.Contains(jdkBinPath))
                    {
                        string newPath = $"{jdkBinPath};{currentPath}";
                        Environment.SetEnvironmentVariable("PATH", newPath, EnvironmentVariableTarget.Process);
                        LogManager.Info("已更新PATH环境变量");
                    }
                    else
                    {
                        LogManager.Info("PATH环境变量已包含JDK路径");
                    }
                    
                    // 配置JAVA_HOME环境变量
                    string javaHome = Path.GetDirectoryName(jdkBinPath);
                    Environment.SetEnvironmentVariable("JAVA_HOME", javaHome, EnvironmentVariableTarget.Process);
                    LogManager.Info($"已设置JAVA_HOME: {javaHome}");
                    
                    // 验证环境变量设置是否成功
                    string testJavaHome = Environment.GetEnvironmentVariable("JAVA_HOME", EnvironmentVariableTarget.Process);
                    if (testJavaHome == javaHome)
                    {
                        LogManager.Info("JAVA_HOME环境变量设置成功");
                    }
                    else
                    {
                        LogManager.Warn("JAVA_HOME环境变量设置可能未生效");
                    }
                    
                    return true;
                }
                catch (Exception ex)
                {
                    LogManager.Error($"配置环境变量时出错: {ex.Message}");
                    LogManager.Error($"错误类型: {ex.GetType().Name}");
                    if (ex is UnauthorizedAccessException)
                    {
                        LogManager.Error("无权限设置环境变量，请以管理员身份运行应用程序");
                    }
                    return false;
                }
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "JdkEnvironmentManager.ConfigureJdkEnvironment");
                LogManager.Error($"配置JDK环境时发生未预期的错误: {ex.Message}");
                LogManager.Error($"错误类型: {ex.GetType().Name}");
                ApplicationStateManager.LogException(ex, "JdkEnvironmentManager.ConfigureJdkEnvironment");
                return false;
            }
        }

        /// <summary>
        /// 查找java可执行文件
        /// </summary>
        /// <returns>java可执行文件路径</returns>
        private static string FindJavaExe()
        {
            // 根据操作系统获取java可执行文件名称
            string javaExeName = Environment.OSVersion.Platform == PlatformID.Unix || 
                                 Environment.OSVersion.Platform == PlatformID.MacOSX ? "java" : "java.exe";
            
            // 常见的java可执行文件路径
            string[] possiblePaths = {
                Path.Combine(JdkExtractPath, "bin", javaExeName),
                Path.Combine(JdkExtractPath, "jdk-25", "bin", javaExeName),
                Path.Combine(JdkExtractPath, "jdk", "bin", javaExeName)
            };
            
            foreach (string path in possiblePaths)
            {
                if (File.Exists(path))
                {
                    return path;
                }
            }
            
            return null;
        }

        /// <summary>
        /// 验证JDK文件结构完整性
        /// </summary>
        /// <returns>JDK文件结构是否完整</returns>
        public static bool ValidateJdkStructure()
        {
            try
            {
                LogManager.Info("验证JDK文件结构完整性");
                
                // 检查JDK目录是否存在
                if (!Directory.Exists(JdkExtractPath))
                {
                    LogManager.Error("JDK目录不存在");
                    return false;
                }
                
                // 查找java.exe
                string javaExePath = FindJavaExe();
                if (string.IsNullOrEmpty(javaExePath))
                {
                    LogManager.Error("找不到java.exe");
                    return false;
                }
                
                // 检查jar.exe是否存在
                string exeExtension = Environment.OSVersion.Platform == PlatformID.Unix || 
                                     Environment.OSVersion.Platform == PlatformID.MacOSX ? "" : ".exe";
                string jarExePath = Path.Combine(Path.GetDirectoryName(javaExePath), $"jar{exeExtension}");
                if (!File.Exists(jarExePath))
                {
                    LogManager.Error($"找不到jar.exe: {jarExePath}");
                    return false;
                }
                
                LogManager.Info("JDK文件结构验证成功");
                return true;
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "JdkEnvironmentManager.ValidateJdkStructure");
                return false;
            }
        }

        /// <summary>
        /// 获取JDK路径
        /// </summary>
        /// <returns>JDK路径</returns>
        public static string GetJdkPath()
        {
            return JdkExtractPath;
        }

        /// <summary>
        /// 清理临时文件
        /// </summary>
        public static void Cleanup()
        {
            try
            {
                // 生产模式下保留临时JDK文件夹，不进行删除
                LogManager.Info("生产模式：保留JDK临时文件");
            }
            catch (Exception ex)
            {
                LogManager.WriteException(ex, "JdkEnvironmentManager.Cleanup");
                ApplicationStateManager.LogException(ex, "JdkEnvironmentManager.Cleanup");
            }
        }
    }
}