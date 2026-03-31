import os

from ollama import chat
import tkinter as tk
from tkinter import scrolledtext,ttk
from tkinter import messagebox
import threading
import time

def init():
    # 检查ollama是否安装
    if os.system("ollama --version") == 0:
        if messagebox.askokcancel("警告", "未检测到ollama，请先安装ollama"):
            # 创建下载窗口
            download_window = tk.Toplevel()
            download_window.title("正在下载Ollama")
            download_window.geometry("400x150")
            download_window.resizable(False, False)
            download_window.transient()
            download_window.grab_set()
            
            # 创建标签
            label = tk.Label(download_window, text="正在下载Ollama安装包...", font=("Microsoft YaHei", 12))
            label.pack(pady=20)
            
            # 创建进度条
            progress_var = tk.DoubleVar()
            progress_bar = ttk.Progressbar(
                download_window, 
                variable=progress_var, 
                maximum=100, 
                length=350, 
                mode='determinate'
            )
            progress_bar.pack(pady=10)
            
            # 创建状态标签
            status_label = tk.Label(download_window, text="准备中...", font=("Microsoft YaHei", 10))
            status_label.pack(pady=5)
            
            # 下载函数
            def download_ollama():
                try:
                    import requests

                    url = 'https://ollama.com/download/OllamaSetup.exe'
                    filename = 'OllamaSetup.exe'
                    
                    status_label.config(text="连接中...")
                    download_window.update()
                    
                    response = requests.get(url, stream=True,verify=False)
                    total_size = int(response.headers.get('content-length', 0))
                    block_size = 1024  # 1KB
                    downloaded_size = 0
                    
                    with open(filename, 'wb') as f:
                        for data in response.iter_content(block_size):
                            downloaded_size += len(data)
                            f.write(data)
                            progress = (downloaded_size / total_size) * 100 if total_size > 0 else 0
                            progress_var.set(progress)
                            status_label.config(text=f"下载中: {progress:.1f}%")
                            download_window.update()
                    
                    status_label.config(text="下载完成，正在启动安装...")
                    download_window.update()
                    
                    os.startfile(filename)
                    download_window.destroy()
                except Exception as e:
                    messagebox.showerror("错误", f"下载失败: {str(e)}")
                    download_window.destroy()
            
            # 启动下载线程
            threading.Thread(target=download_ollama).start()
    
    # 检查并拉取模型
    def check_and_pull_model():
        # 创建模型检查窗口
        model_window = tk.Toplevel()
        model_window.title("正在检查模型")
        model_window.geometry("400x150")
        model_window.resizable(False, False)
        model_window.transient()
        model_window.grab_set()
        
        # 创建标签
        label = tk.Label(model_window, text="正在检查模型...", font=("Microsoft YaHei", 12))
        label.pack(pady=20)
        
        # 创建进度条
        progress_var = tk.DoubleVar()
        progress_bar = ttk.Progressbar(
            model_window, 
            variable=progress_var, 
            maximum=100, 
            length=350, 
            mode='indeterminate'
        )
        progress_bar.pack(pady=10)
        progress_bar.start()
        
        # 检查函数
        def check_model():
            try:
                # 检查模型是否存在
                if os.system("ollama show 1473443474/tsukimi-yachiyo") != 0:
                    label.config(text="模型不存在，正在拉取...")
                    model_window.update()
                    
                    # 拉取模型
                    os.system("ollama pull 1473443474/tsukimi-yachiyo")
                
                label.config(text="模型检查完成")
                while os.system("ollama show 1473443474/tsukimi-yachiyo") != 0:
                    time.sleep(1)
                    model_window.update()
                model_window.destroy()
            except Exception as e:
                messagebox.showerror("错误", f"模型检查失败: {str(e)}")
                model_window.destroy()
        
        # 启动检查线程
        threading.Thread(target=check_model).start()
    
    # 启动模型检查
    check_and_pull_model()

class YachiyoClient:
    def __init__(self, root):
        self.root = root
        self.root.title("月见八千代 - 智能助手")
        self.root.geometry("800x660")
        self.root.resizable(True, True)

        self.root.iconbitmap("icon.ico")

        # 设置主题颜色
        self.bg_color = "#f5f5f5"
        self.chat_bg = "#ffffff"
        self.user_color = "#e6f7ff"
        self.ai_color = "#f0f0f0"
        self.input_bg = "#ffffff"
        self.send_btn_bg = "#1890ff"
        self.send_btn_fg = "#ffffff"


        init()

        # 聊天历史
        self.messages = []

        # 创建主框架
        self.main_frame = tk.Frame(root, bg=self.bg_color)
        self.main_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

        # 创建聊天区域
        self.chat_frame = tk.Frame(self.main_frame, bg=self.chat_bg, bd=1, relief=tk.SUNKEN)
        self.chat_frame.pack(fill=tk.BOTH, expand=True, pady=(0, 10))

        # 创建滚动文本框
        self.chat_text = scrolledtext.ScrolledText(
            self.chat_frame,
            wrap=tk.WORD,
            bg=self.chat_bg,
            fg="#333333",
            font=("Microsoft YaHei", 12),
            bd=0,
            highlightthickness=0
        )
        self.chat_text.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        self.chat_text.config(state=tk.DISABLED)

        # 创建输入区域
        self.input_frame = tk.Frame(self.main_frame, bg=self.bg_color)
        self.input_frame.pack(fill=tk.X, pady=(0, 10))
        
        # 创建输入文本框
        self.input_text = scrolledtext.ScrolledText(
            self.input_frame, 
            wrap=tk.WORD, 
            height=16,
            width=5,
            bg=self.input_bg, 
            fg="#333333",
            font=("Microsoft YaHei", 12),
            bd=1, 
            relief=tk.SUNKEN,
            highlightthickness=0
        )
        self.input_text.pack(fill=tk.BOTH, expand=True, side=tk.LEFT, padx=(0, 10))
        
        # 创建按钮框架
        self.btn_frame = tk.Frame(self.input_frame, bg=self.bg_color, width=80)
        self.btn_frame.pack(side=tk.RIGHT, fill=tk.Y)
        self.btn_frame.pack_propagate(False)  # 防止框架大小被内容改变

        # 创建发送按钮
        self.send_btn = tk.Button(
            self.btn_frame,
            text="发送",
            bg=self.send_btn_bg,
            fg=self.send_btn_fg,
            font=("Microsoft YaHei", 12, "bold"),
            bd=0,
            relief=tk.FLAT,
            command=self.send_message
        )
        self.send_btn.pack(fill=tk.X, pady=(0, 5), padx=5)

        # 创建清空按钮
        self.clear_btn = tk.Button(
            self.btn_frame,
            text="清空",
            bg="#f0f0f0",
            fg="#333333",
            font=("Microsoft YaHei", 10),
            bd=0,
            relief=tk.FLAT,
            command=self.clear_chat
        )
        self.clear_btn.pack(fill=tk.X, pady=(0, 5),padx=5)

        # 绑定回车键发送消息
        self.input_text.bind("<Return>", self.on_enter)
        self.input_text.bind("<Shift-Return>", self.on_shift_enter)

        # 添加欢迎消息
        self.add_message("assistant", "你好！我是月见八千代，有什么我可以帮助你的吗？")

    def on_enter(self, event):
        # 回车键发送消息
        if not event.state & 0x0001:  # 不是Shift键
            self.send_message()
            return "break"
        return None

    def on_shift_enter(self, event):
        # Shift+回车键换行
        return None

    def add_message(self, role, content):
        # 添加消息到聊天窗口
        self.messages.append({"role": role, "content": content})

        # 更新聊天文本
        self.chat_text.config(state=tk.NORMAL)

        if role == "user":
            # 用户消息
            self.chat_text.insert(tk.END, "你: \n", "user_tag")
            self.chat_text.insert(tk.END, content + "\n\n", "user_content")
        else:
            # AI消息
            self.chat_text.insert(tk.END, "月见八千代: \n", "ai_tag")
            self.chat_text.insert(tk.END, content + "\n\n", "ai_content")

        # 配置标签样式
        self.chat_text.tag_configure("user_tag", font=("Microsoft YaHei", 12, "bold"), foreground="#1890ff")
        self.chat_text.tag_configure("ai_tag", font=("Microsoft YaHei", 12, "bold"), foreground="#52c41a")
        self.chat_text.tag_configure("user_content", font=("Microsoft YaHei", 12), foreground="#333333")
        self.chat_text.tag_configure("ai_content", font=("Microsoft YaHei", 12), foreground="#333333")

        self.chat_text.config(state=tk.DISABLED)
        # 滚动到底部
        self.chat_text.see(tk.END)

    def send_message(self):
        # 获取用户输入
        user_input = self.input_text.get(1.0, tk.END).strip()
        if not user_input:
            return

        # 清空输入框
        self.input_text.delete(1.0, tk.END)

        # 添加用户消息到聊天窗口
        self.add_message("user", user_input)

        # 禁用发送按钮
        self.send_btn.config(state=tk.DISABLED, text="思考中...")

        # 在新线程中发送消息并获取回复
        def get_response():
            try:
                response = chat(
                    model='1473443474/tsukimi-yachiyo',
                    messages=self.messages,
                )
                # 添加AI回复到聊天窗口
                self.add_message("assistant", response.message.content)
            except Exception as e:
                self.add_message("assistant", f"抱歉，发生错误：{str(e)}")
            finally:
                # 恢复发送按钮
                self.send_btn.config(state=tk.NORMAL, text="发送")

        # 启动线程
        threading.Thread(target=get_response).start()

    def clear_chat(self):
        # 清空聊天历史
        self.messages = []
        self.chat_text.config(state=tk.NORMAL)
        self.chat_text.delete(1.0, tk.END)
        self.chat_text.config(state=tk.DISABLED)
        # 添加欢迎消息
        self.add_message("assistant", "你好！我是月见八千代，有什么我可以帮助你的吗？")

if __name__ == "__main__":
    root = tk.Tk()
    app = YachiyoClient(root)
    root.mainloop()