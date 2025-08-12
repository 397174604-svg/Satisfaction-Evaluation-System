package com.example.gbmzcpb.controller;

import com.example.gbmzcpb.entity.Person;
import com.example.gbmzcpb.entity.SatisfactionItem;
import com.example.gbmzcpb.jpa.PersonRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import com.example.gbmzcpb.entity.SatisfactionCategory;
import com.example.gbmzcpb.jpa.SatisfactionCategoryRepository;
import com.example.gbmzcpb.jpa.SatisfactionItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sj")
public class CategoryController {

    @GetMapping
    public String defaltPath() {
        return "redirect:/sj/list";
    }

    @Autowired
    private SatisfactionCategoryRepository CaRepository;
    @Autowired
    private SatisfactionItemRepository ItemRepository;

    //显示所有分类
    @GetMapping("/list")
    public String listCategory(Model model) {
        List<SatisfactionCategory> CategoryList = CaRepository.findAll();
        model.addAttribute("CategoryList", CategoryList);
        return "category";
    }

    // 显示添加/编辑课程表单
    @GetMapping("/form")
    public String showForm(Model model, @RequestParam(required = false) Integer id) {
        SatisfactionCategory category = id == null ? new SatisfactionCategory() : CaRepository.findById(id).orElse(new SatisfactionCategory());
        model.addAttribute("category", category);
        SatisfactionItem item = id == null ? new SatisfactionItem() : ItemRepository.findById(id).orElse(new SatisfactionItem());
        model.addAttribute("item", item);
        return "category_form";
    }

    // 保存类型
    @PostMapping("/save")
    public String saveCourse(@ModelAttribute SatisfactionCategory category) {
        CaRepository.save(category);

        return "redirect:/sj/list";
    }

    @Autowired
    private PersonRepository personRepository;

    // 删除课程
    @GetMapping("/delete")
    @Transactional
    public String deleteCourse(@RequestParam Integer id) {
        CaRepository.deleteById(id);
        return "redirect:/sj/list";
    }

    // 搜索课程
    @GetMapping("/c_search")
    public String searchCourses(Model model, @RequestParam String name) {
        List<SatisfactionCategory> Cate = CaRepository.findBynameContaining(name);
        model.addAttribute("CategoryList", Cate);
        return "category";
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 显示添加/编辑课程表单
    @GetMapping("/itemform")
    public String showitemForm(Model model, 
                              @RequestParam(required = false) Integer id,
                              @RequestParam(required = false) Integer categoryId) {
        SatisfactionItem item = id == null ? new SatisfactionItem() : ItemRepository.findById(id).orElse(new SatisfactionItem());
        
        // 如果是新建项目且提供了categoryId，设置对应的category
        if (id == null && categoryId != null) {
            SatisfactionCategory category = CaRepository.findById(categoryId).orElse(null);
            if (category != null) {
                item.setCategory(category);
            }
        }
        
        // 将item和对应的category添加到模型中
        model.addAttribute("item", item);
        model.addAttribute("category", item.getCategory());
        
        return "category_item_form";
    }
    @GetMapping("/itemlist")
    public String itemlistCategory(Model model, @RequestParam(required = false) Integer categoryId) {
        List<SatisfactionItem> itemList;
        if (categoryId != null) {
            // 如果提供了categoryId，只获取该category下的items
            SatisfactionCategory category = CaRepository.findById(categoryId).orElse(null);
            if (category != null) {
                itemList = ItemRepository.findByCategory(category);
                model.addAttribute("category", category);
            } else {
                itemList = new ArrayList<>();
            }
        } else {
            // 如果没有提供categoryId，获取所有items
            itemList = ItemRepository.findAll();
        }
        model.addAttribute("itemList", itemList);
        return "category_item";
    }
    @PostMapping("/saveItem")
    public String saveItem(@ModelAttribute SatisfactionItem item, @RequestParam Integer categoryId) {
        // 根据categoryId获取Category对象
        SatisfactionCategory category = CaRepository.findById(categoryId).orElse(null);
        if (category != null) {
            // 设置item的category关联
            item.setCategory(category);
        }
        ItemRepository.save(item);
        // 重定向时传递categoryId参数
        return "redirect:/sj/itemlist?categoryId=" + categoryId;
    }

    @GetMapping("/itemdelete")
    @Transactional
    public String deleteItem(@RequestParam Integer id, @RequestParam Integer categoryId) {
        // 先查找所有关联该选项的人员
        List<Person> persons = personRepository.findBySatisfactionItems_Id(id);

        // 从这些人员中移除该选项
        for (Person person : persons) {
            person.getSatisfactionItems().removeIf(item -> item.getId().equals(id));
            personRepository.save(person);
        }

        // 现在可以安全删除选项
        ItemRepository.deleteById(id);
        return "redirect:/sj/itemlist?categoryId=" + categoryId;
    }

    // 搜索选项
    @GetMapping("/i_search")
    public String searchi(Model model, @RequestParam String name, @RequestParam(required = false) Integer categoryId) {
        List<SatisfactionItem> items = ItemRepository.findByContentContaining(name);
        model.addAttribute("itemList", items);
        
        // 如果提供了categoryId，添加category对象到模型中
        if (categoryId != null) {
            SatisfactionCategory category = CaRepository.findById(categoryId).orElse(null);
            model.addAttribute("category", category);
        }
//        if (name != null) {
//
//            return "redirect:/sj/itemlist";
//        }
        
        return "category_item";
    }


}
