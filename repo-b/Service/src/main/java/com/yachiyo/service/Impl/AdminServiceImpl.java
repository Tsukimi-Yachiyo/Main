package com.yachiyo.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yachiyo.Config.SecuritySafeToolConfig;
import com.yachiyo.Utils.JwtUtils;
import com.yachiyo.dto.PostingQueryRequest;
import com.yachiyo.dto.ReviewRequest;
import com.yachiyo.entity.Posting;
import com.yachiyo.entity.User;
import com.yachiyo.enumeration.PostingStatus;
import com.yachiyo.enumeration.ReviewAction;
import com.yachiyo.mapper.PostingMapper;
import com.yachiyo.result.Result;
import com.yachiyo.service.AdminService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openaisdk.OpenAiSdkChatModel;
import org.springframework.ai.openaisdk.OpenAiSdkChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import static com.baomidou.mybatisplus.extension.spi.SpringCompatibleSet.applicationContext;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SecuritySafeToolConfig securitySafeToolConfig;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public Result<String> Login(User user) {
        try {
            if (user.getName().equals("admin")) {
                if (user.getPassword().equals(adminPassword)) {
                    return Result.success(jwtUtils.generateToken(0L, "admin", securitySafeToolConfig.getUnique(0L)));
                }
            }
        } catch (Exception e) {
            return Result.error("500", "登录失败", e.getMessage());
        }
        return Result.error("400", "登录失败", "登录失败");
    }

    @Override
    public Result<Long> GetRemainingToken() {
        return Result.success(0L);
    }

    @Value("${spring.ai.openai-sdk.chat.options.temperature}")
    private String temperature;

    @Value("${spring.ai.openai-sdk.chat.options.top-p}")
    private String topP;

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private Advisor retrievalAugmentationAdvisor;

    @Autowired
    private PostingMapper postingMapper;

    @Override
    public Result<Void> ChangeApiKey(String apiKey, String model) {
        OpenAiSdkChatOptions openAiSdkChatOptions = new OpenAiSdkChatOptions();
        openAiSdkChatOptions.setApiKey(apiKey);
        openAiSdkChatOptions.setModel(model);
        openAiSdkChatOptions.setTemperature(Double.valueOf(temperature));
        openAiSdkChatOptions.setTopP(Double.valueOf(topP));
        OpenAiSdkChatModel openAiSdkChatModel = new OpenAiSdkChatModel(openAiSdkChatOptions);

        ChatClient chatClient = ChatClient.builder(openAiSdkChatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        retrievalAugmentationAdvisor
                )
                .build();

        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();

        // 1. 销毁旧 Bean
        beanFactory.destroySingleton("ChatModel");
        // 2. 注册新 Bean
        beanFactory.registerSingleton("ChatModel", chatClient);
        return Result.success(null);
    }

    @Override @SuppressWarnings("all")
    public Result<String> RunCommand(String command) {
        try {
            // 执行命令
            Process process = Runtime.getRuntime().exec(command);

            // 读取命令执行结果（标准输出）
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            System.out.println("=== 命令执行结果 ===");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 读取错误输出（重要！排查问题用）
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );
            System.out.println("\n=== 错误信息（如有）===");
            while ((line = errorReader.readLine()) != null) {
                return Result.error("400", "执行命令失败", line);
            }

            // 等待命令执行完成，获取退出码 0=成功
            int exitCode = process.waitFor();
            return Result.success(String.valueOf(exitCode));

        } catch (Exception e) {
            return Result.error("400", "执行命令失败", e.getMessage());
        }
    }

    @Override
    public Result<Boolean> reviewPosting(ReviewRequest request) {
        try {
            Long postingId = request.getPostingId();
            ReviewAction action = request.getAction();

            if (action == ReviewAction.DELETE) {
                // 删除帖子
                postingMapper.deleteById(postingId);
                return Result.success(true);
            }

            Posting posting = new Posting();
            posting.setId(postingId);
            if (action == ReviewAction.APPROVE) {
                posting.setIsApproved(true);
            } else if (action == ReviewAction.REJECT) {
                posting.setIsApproved(false);
            }
            postingMapper.updateById(posting);
            return Result.success(true);
        } catch (Exception e) {
            return Result.error("400", "审核帖子失败", e.getMessage());
        }
    }

    @Override
    public Result<List<Posting>> queryPostings(PostingQueryRequest request) {
        try {
            LambdaQueryWrapper<Posting> queryWrapper = new LambdaQueryWrapper<>();

            // 状态筛选
            PostingStatus status = request.getStatus();
            if (status != null && status != PostingStatus.ALL) {
                if (status == PostingStatus.PENDING) {
                    queryWrapper.isNull(Posting::getIsApproved);
                } else if (status == PostingStatus.APPROVED) {
                    queryWrapper.eq(Posting::getIsApproved, true);
                } else if (status == PostingStatus.REJECTED) {
                    queryWrapper.eq(Posting::getIsApproved, false);
                }
            }

            // 关键词搜索
            String keyword = request.getKeyword();
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.and(wrapper -> wrapper
                    .like(Posting::getTitle, "%" + keyword + "%")
                    .or()
                    .like(Posting::getContent, "%" + keyword + "%")
                );
            }

            // 分页
            Integer pageNum = request.getPageNum();
            Integer pageSize = request.getPageSize();
            if (pageNum != null && pageSize != null && pageNum > 0 && pageSize > 0) {
                queryWrapper.last("LIMIT " + pageSize + " OFFSET " + (pageNum - 1) * pageSize);
            }

            List<Posting> postings = postingMapper.selectList(queryWrapper);
            return Result.success(postings);
        } catch (Exception e) {
            return Result.error("400", "查询帖子失败", e.getMessage());
        }
    }
}
