package com.nowcoder.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换敏感词汇
    private static final String REPLACEMENT = "***";

    //初始化根节点
    private TrieNode rootNode = new TrieNode();

    //定义一个初始化方法
    @PostConstruct
    public void init(){
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while ((keyword = reader.readLine())!=null){
                //添加到前缀树
                this.addKeyword(keyword);
            }
        }catch (IOException e){
            logger.error("加载敏感词文件失败："+e.getMessage());
        }

    }

    //将一个敏感词添加到前缀树中
    public void addKeyword(String keyword){
        //将根节点作为当前节点
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            //判断根节点下是否存在c子节点
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode==null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //指向子节点，进入下一轮循环
            tempNode = subNode;

            //设置结束标识
            if (i == keyword.length()-1){
                tempNode.setKeyWordEnd(true);
            }

        }
    }

    //过滤敏感词的方法
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }
        //指针1
        TrieNode tempNode = new TrieNode();
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while(position<text.length()){
            char c = text.charAt(position);

            //跳过符号
            if (isSymbol(c)){
                //若指针1处于根节点,将此符号计入结果，让指针2向下走一步
                if (tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode==null){
                //以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                position = ++begin;
                //重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd) {
                //发现敏感词，将begin——position字符串替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin = ++position;
                //重新指向根节点
                tempNode = rootNode;
            }else {
                //检查下一个字符
                position++;
            }
        }
        //将最后一批字符计入结果
        sb.append(text.substring(begin));
        return sb.toString();
    }
    //判断是否为符号
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //定义前缀树节点类
    private class TrieNode {
        //是否是关键词最后一个字符
        private boolean isKeyWordEnd = false;
        //描述子节点(key是下级字符，value是下级节点)
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        //添加子节点的方法
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        //获取子节点的方法
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }



}
