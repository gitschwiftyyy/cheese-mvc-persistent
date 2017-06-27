package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CategoryDao;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static javax.swing.text.StyleConstants.ModelAttribute;

/**
 * Created by schwifty on 6/22/17.
 */

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value="")
    public String index(Model model){
        model.addAttribute("title", "Menu");
        model.addAttribute("menus", menuDao.findAll());

        return "menu/index";

    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("menu", menu);
        model.addAttribute(new AddMenuItemForm());

        return "menu/view";

    }
    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        Iterable<Cheese> cheeses = cheeseDao.findAll();
        AddMenuItemForm addMenuItemForm = new AddMenuItemForm();
        addMenuItemForm.setMenu(menu);
        addMenuItemForm.setCheeses(cheeses);

        model.addAttribute("title", "Add Item to Menu: " + menu.getName());
        model.addAttribute("form", addMenuItemForm);
        model.addAttribute("menu", menu);

        return "menu/add-item";
    }

    @RequestMapping(value="/add-item", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm addMenuItemForm,
                          @RequestParam (value="cheeseId", required = true) int cheeseId,
                          @RequestParam (value="menuId", required = true) int menuId,
                                  Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Item to Menu");
             return "menu/add-item";
        }

        Cheese cheese = cheeseDao.findOne(cheeseId);
        Menu menu = menuDao.findOne(menuId);
        menu.addItem(cheese);
        menuDao.save(menu);

        return "redirect:/menu/view/" + menu.getId();
    }
}












