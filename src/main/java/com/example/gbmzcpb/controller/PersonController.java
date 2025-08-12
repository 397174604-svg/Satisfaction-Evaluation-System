package com.example.gbmzcpb.controller;

import com.example.gbmzcpb.entity.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import com.example.gbmzcpb.jpa.PersonRepository;
import com.example.gbmzcpb.jpa.SatisfactionCategoryRepository;
import com.example.gbmzcpb.jpa.SatisfactionItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private SatisfactionCategoryRepository categoryRepository;

    @Autowired
    private SatisfactionItemRepository itemRepository;

    @GetMapping("/add")
    public String person(String name, Model model) {
        // 初始化群众满意度类别和选项
        SatisfactionCategory qzCategory = categoryRepository.findByCode("QZ");
        if (qzCategory == null) {
            qzCategory = new SatisfactionCategory();
            qzCategory.setName("群众满意度");
            qzCategory.setCode("QZ");
            qzCategory = categoryRepository.save(qzCategory);

            // 创建群众满意度选项
            String[] qzItems = {
                "接待群众热情、耐心",
                "群众反馈问题及时处理",
                "积极追踪群众间大事小情，化矛盾于无形"
            };

            for (String content : qzItems) {
                SatisfactionItem item = new SatisfactionItem();
                item.setContent(content);
                item.setCategory(qzCategory);
                itemRepository.save(item);
            }
        }

        // 初始化行政满意度类别和选项
        SatisfactionCategory xzCategory = categoryRepository.findByCode("XZ");
        if (xzCategory == null) {
            xzCategory = new SatisfactionCategory();
            xzCategory.setName("行政满意度");
            xzCategory.setCode("XZ");
            xzCategory = categoryRepository.save(xzCategory);

            // 创建行政满意度选项
            String[] xzItems = {
                "业务知识精通",
                "敢于担当，不推诿",
                "协同能力突出，组织能力强"
            };

            for (String content : xzItems) {
                SatisfactionItem item = new SatisfactionItem();
                item.setContent(content);
                item.setCategory(xzCategory);
                itemRepository.save(item);
            }
        }

        // 获取所有满意度类别
        List<SatisfactionCategory> categories = categoryRepository.findAll();
        
        // 按类别分组获取所有满意度项目
        Map<String, List<SatisfactionItem>> itemsByCategory = new LinkedHashMap<>();
        for (SatisfactionCategory category : categories) {
            itemsByCategory.put(category.getCode(), 
                itemRepository.findByCategoryCode(category.getCode()));
        }

        model.addAttribute("categories", categories);
        model.addAttribute("itemsByCategory", itemsByCategory);

        // 如果传入了name参数，说明是编辑操作
        if (name != null && !name.isEmpty()) {
            Person person = personRepository.findByName(name);
            if (person != null) {
                model.addAttribute("person", person);
            }
        }

        return "person";  // 返回视图名称
    }

    @GetMapping("/browse")
    public String browse(HttpServletRequest request, Model model){
        // 获取所有满意度类别
        List<SatisfactionCategory> categories = categoryRepository.findAll();
        
        // 构建表头：基本信息
//        List<String> headers = new ArrayList<>();
//        headers.addAll(Arrays.asList("姓名", "年龄", "性别"));
        
        // 存储所有满意度项目，按类别分组
        Map<String, List<SatisfactionItem>> itemsByCategory = new LinkedHashMap<>();
        
        // 添加每个类别的满意度项目到表头
        for (SatisfactionCategory category : categories) {
            //调用itemRepository.findByCategory方法获取该类别下的所有满意度选项
            List<SatisfactionItem> items = itemRepository.findByCategory(category);
            if (!items.isEmpty()) {
                //设置哈希列表 category.getName() 获取键值满意度项目，items对应的满意度选项
                itemsByCategory.put(category.getName(), items);
                //forEach对 items 列表中的每个满意度项目（item）执行操作，
                //调用 getContent() 方法获取该项目的内容（通常是这个项目的标题或描述）。
//                items.forEach(item -> headers.add(item.getContent()));
            }
        }
        
//        model.addAttribute("head", headers);
        model.addAttribute("categories", categories);
        model.addAttribute("itemsByCategory", itemsByCategory);

        // 从session中获取用户职业和用户名
        String occupation = (String) request.getSession().getAttribute("occupation");
        String username = (String) request.getSession().getAttribute("user");

        if ("管理员".equals(occupation)||"admin".equals(occupation)) {
            // 如果是教师，显示所有人的信息
            model.addAttribute("persons", personRepository.findAll());
        } else {
            // 如果是学生，只显示自己的信息
            Person person = personRepository.findByName(username);
            if (person != null) {
                model.addAttribute("persons", Arrays.asList(person));
            } else {
                model.addAttribute("persons", Arrays.asList());
            }
        }
        return "browse";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String sex,
            @RequestParam MultiValueMap<String, String> allParams,
            Model model) {

        try {
            // 获取或创建Person对象
            Person person = personRepository.findByName(name);
            if (person == null) {
                person = new Person();
                person.setName(name);
            }

            // 设置基本信息
            person.setAge(age);
            person.setSex(sex);

            // 初始化满意度项目列表
            List<SatisfactionItem> satisfactionItems = new ArrayList<>();

            // 获取所有满意度类别
            List<SatisfactionCategory> categories = categoryRepository.findAll();

            // 打印所有接收到的参数用于调试
            System.out.println("接收到的所有参数:");
            allParams.forEach((key, values) -> 
                System.out.println(key + ": " + values)
            );

            // 处理每个类别的满意度项目
            for (SatisfactionCategory category : categories) {
                String paramName = category.getCode().toLowerCase() + "Items";
                List<String> itemIds = allParams.get(paramName);
                
                if (itemIds != null) {
                    for (String itemId : itemIds) {
                        try {
                            Integer id = Integer.parseInt(itemId);
                            itemRepository.findById(id)
                                .ifPresent(satisfactionItems::add);
                        } catch (NumberFormatException e) {
                            System.out.println("无效的项目ID: " + itemId + " 对于类别: " + category.getCode());
                        }
                    }
                }
            }

            // 设置满意度项目
            person.setSatisfactionItems(satisfactionItems);

            // 保存Person对象
            personRepository.save(person);

            return "redirect:/browse";

        } catch (Exception e) {
            e.printStackTrace(); // 打印完整的堆栈跟踪
            model.addAttribute("error", "保存失败: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/delete")
    public String del(String name, Model model){
        Person person = personRepository.findByName(name);  // 先查找要删除的Person
        if (person != null) {
            personRepository.delete(person);  // 使用JPA Repository从数据库中删除
        }
        return "redirect:/browse";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String sex,
            @RequestParam MultiValueMap<String, String> allParams,
            HttpServletRequest request, 
            Model model) {

        // 获取所有满意度类别和项目
        List<SatisfactionCategory> categories = categoryRepository.findAll();

        // 存储所有满意度项目，按类别分组
        Map<String, List<SatisfactionItem>> itemsByCategory = new LinkedHashMap<>();
        for (SatisfactionCategory category : categories) {
            itemsByCategory.put(category.getName(),
                    itemRepository.findByCategory(category));
        }

        // 构建表头：基本信息和满意度项目
        List<String> headers = new ArrayList<>(Arrays.asList("姓名", "年龄", "性别"));
        itemsByCategory.values().forEach(items ->
                items.forEach(item -> headers.add(item.getContent()))
        );

        model.addAttribute("head", headers);
        model.addAttribute("categories", categories);
        model.addAttribute("itemsByCategory", itemsByCategory);

        // 从session中获取用户职业和用户名
        String occupation = (String) request.getSession().getAttribute("occupation");
        String username = (String) request.getSession().getAttribute("user");

        if ("管理员".equals(occupation)) {
            // 获取所有人员作为基础数据集
            List<Person> searchResults = personRepository.findAll();

            // 从allParams中提取基本查询参数
            String name1 = allParams.getFirst("name");
            String ageStr = allParams.getFirst("age");
            String sex1 = allParams.getFirst("sex");
            Integer age1 = ageStr != null ? Integer.parseInt(ageStr) : null;

            // 应用基本查询条件过滤
            if (name != null) {
                searchResults = searchResults.stream()
                        .filter(p -> p.getName().contains(name1))
                        .collect(Collectors.toList());
            }
            if (age != null) {
                searchResults = searchResults.stream()
                        .filter(p -> p.getAge() == age1)
                        .collect(Collectors.toList());
            }
            if (sex != null) {
                searchResults = searchResults.stream()
                        .filter(p -> sex1.equals(p.getSex()))
                        .collect(Collectors.toList());
            }

            // 处理满意度查询条件
            for (SatisfactionCategory category : categories) {
                String paramName = category.getCode().toLowerCase() + "Items";
                List<String> itemIds = allParams.get(paramName);

                if (itemIds != null && !itemIds.isEmpty()) {
                    searchResults = searchResults.stream()
                            .filter(p -> p.getSatisfactionItems().stream()
                                    .anyMatch(item -> itemIds.contains(item.getId().toString())))
                            .collect(Collectors.toList());
                }
            }

            model.addAttribute("persons", searchResults);
        } else {
            // 如果是普通用户，只能看到自己的信息，忽略搜索参数
            Person person = personRepository.findByName(username);
            if (person != null) {
                model.addAttribute("persons", Arrays.asList(person));
            } else {
                model.addAttribute("persons", Arrays.asList());
            }
        }
        return "browse";
    }
}