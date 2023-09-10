package com.code.challenge.calories.app.views.meals;

import com.code.challenge.calories.app.client.order.entity.Order;
import com.code.challenge.calories.app.client.product.entity.Product;
import com.code.challenge.calories.app.client.product.entity.ProductType;
import com.code.challenge.calories.app.service.OrderService;
import com.code.challenge.calories.app.service.ProductOrder;
import com.code.challenge.calories.app.service.ProductService;
import com.code.challenge.calories.app.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;
import java.util.Optional;

@PageTitle("Meals")
@Route(value = "meals/:orderID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class MealsView extends Div implements BeforeEnterObserver {

    private final String ORDER_ID = "orderID";
    private final String ORDER_EDIT_ROUTE_TEMPLATE = "meals/%s/edit";

    private final Grid<Order> grid = new Grid<>(Order.class, false);

    private TextField user;
    private ComboBox<Product> mainCourse;
    private ComboBox<Product> beverage;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<ProductOrder> binder;

    private ProductOrder order;

    private final OrderService orderService;
    private final ProductService productService;

    public MealsView(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
        addClassNames("meals-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("user").setAutoWidth(true);
        grid.addColumn("mainCourse").setAutoWidth(true);
        grid.addColumn("beverage").setAutoWidth(true);
        grid.addColumn("calories").setAutoWidth(true);
        grid.setItems(orderService.listOrders());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ORDER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MealsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(ProductOrder.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.order == null) {
                    this.order = new ProductOrder();
                }
                binder.writeBean(this.order);
                orderService.addOrder(this.order);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(MealsView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> orderId = event.getRouteParameters().get(ORDER_ID).map(Long::parseLong);
        if (orderId.isPresent()) {
            Optional<ProductOrder> orderFromBackend = orderService.getOrder(orderId.get());
            if (orderFromBackend.isPresent()) {
                populateForm(orderFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested order was not found, ID = %s", orderId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MealsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        user = new TextField("User");
        mainCourse = new ComboBox("Main Course");
        beverage = new ComboBox("Beverage");
        formLayout.add(user, mainCourse, beverage);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);

        List<Product> items = productService.listProducts();
        List<Product> mainCourses = items.stream().filter(item -> item.getType() == ProductType.MAIN_COURSE).toList();
        List<Product> beverages = items.stream().filter(item -> item.getType() == ProductType.BEVERAGE).toList();
        mainCourse.setItems(mainCourses);
        mainCourse.setItemLabelGenerator(Product::getName);
        beverage.setItems(beverages);
        beverage.setItemLabelGenerator(Product::getName);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.setItems(orderService.listOrders());
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(ProductOrder value) {
        this.order = value;
        binder.readBean(this.order);

    }
}
