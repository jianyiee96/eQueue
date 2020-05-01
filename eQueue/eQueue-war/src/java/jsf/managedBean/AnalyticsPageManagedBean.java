package jsf.managedBean;

import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import entity.CustomerOrder;
import entity.MenuItem;
import entity.OrderLineItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;

@Named(value = "analyticsPageManagedBean")
@ViewScoped
public class AnalyticsPageManagedBean implements Serializable {

    @EJB
    private CustomerOrderSessionBeanLocal customerOrderSessionBean;

    @EJB
    private MenuItemSessionBeanLocal menuItemSessionBean;

    private List<CustomerOrder> customerOrders;
    private String optionType;
    private String optionMenuItemsType;
    private String optionDateType;
    private List<MenuItem> menuItems;
    private Long selectedMenuItemId;
    private MenuItem selectedMenuItem;
    private Date selectedDate;

    private Boolean initialized;

    private BarChartModel allMenuItemsBarChart;

    public AnalyticsPageManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        initialized = false;
        optionType = "MENU_ITEMS";
        optionMenuItemsType = "ALL";
        optionDateType = "DAY";
        menuItems = menuItemSessionBean.retrieveAllMenuItems();
        customerOrders = customerOrderSessionBean.retrieveAllOrders();

        this.selectedMenuItemId = null;

    }

    public void processFilter() {
        System.out.println("PROCESSING ==============================");
        initialized = true;
        System.out.println("OptionType: " + optionType);
        System.out.println("MenuItemsSelectionType: " + optionMenuItemsType);
        if (selectedMenuItemId != null) {
            System.out.println("SelectedMenuItemId: " + selectedMenuItemId);
        }
        System.out.println("optionDateType: " + optionDateType);
        System.out.println("selectedDate: " + selectedDate);

        if (optionType.equals("MENU_ITEMS")) {
            if (optionMenuItemsType.equals("ALL")) {
                initAllMenuItemsBarChart();
            } else if (optionMenuItemsType.equals("SINGLE")) {

            }
        } else if (optionType.equals("SALES_REVENUE")) {

        } else if (optionType.equals("POPULAR_TIMINGS")) {

        }
        System.out.println("PROCESSING ENDED==============================");
    }

    private void initAllMenuItemsBarChart() {
        System.out.println("==============================ENTERED INIT==============================");

        allMenuItemsBarChart = new BarChartModel();
        ChartData data = new ChartData();
        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Menu Items");
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        List<CustomerOrder> orders = filterByDateRange();
        Map<MenuItem, Integer> map = new HashMap<>();

        for (CustomerOrder customerOrder : orders) {
            for (OrderLineItem orderLineItem : customerOrder.getOrderLineItems()) {
                MenuItem menuItem = orderLineItem.getMenuItem();
                int count = map.containsKey(menuItem) ? map.get(menuItem) : 0;
                map.put(menuItem, count + Math.toIntExact(orderLineItem.getQuantity()));
            }
        }

        for (Map.Entry<MenuItem, Integer> entry : map.entrySet()) {
            System.out.println("Added into Series: " + entry.getKey());
            labels.add(entry.getKey().getMenuItemName());
            values.add(entry.getValue());
//            menuItemsSeries.set(entry.getKey().getMenuItemName(), entry.getValue());
        }
        barDataSet.setData(values);
        data.setLabels(labels);
        allMenuItemsBarChart.setData(data);

        System.out.println("==============================EXIT INIT==============================");
    }

    private BarChartModel barModel;

    public BarChartModel getBarModel() {
        barModel = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("My First Dataset");

        List<Number> values = new ArrayList<>();
        values.add(65);
        values.add(59);
        values.add(80);
        values.add(81);
        values.add(56);
        values.add(55);
        values.add(40);
        barDataSet.setData(values);

        List<String> bgColor = new ArrayList<>();
        bgColor.add("rgba(255, 99, 132, 0.2)");
        bgColor.add("rgba(255, 159, 64, 0.2)");
        bgColor.add("rgba(255, 205, 86, 0.2)");
        bgColor.add("rgba(75, 192, 192, 0.2)");
        bgColor.add("rgba(54, 162, 235, 0.2)");
        bgColor.add("rgba(153, 102, 255, 0.2)");
        bgColor.add("rgba(201, 203, 207, 0.2)");
        barDataSet.setBackgroundColor(bgColor);

        List<String> borderColor = new ArrayList<>();
        borderColor.add("rgb(255, 99, 132)");
        borderColor.add("rgb(255, 159, 64)");
        borderColor.add("rgb(255, 205, 86)");
        borderColor.add("rgb(75, 192, 192)");
        borderColor.add("rgb(54, 162, 235)");
        borderColor.add("rgb(153, 102, 255)");
        borderColor.add("rgb(201, 203, 207)");
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        labels.add("July");
        data.setLabels(labels);

        //Data
        barModel.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        barModel.setOptions(options);
        return barModel;
    }

    private List<CustomerOrder> filterByDateRange() {
        Date date1 = selectedDate;
        int year1 = date1.getYear();
        int month1 = date1.getMonth();
        int day1 = date1.getDate();

        List<CustomerOrder> list = new ArrayList<>(customerOrders);
        List<CustomerOrder> resultList = new ArrayList<>();

        if (optionDateType.equals("DAY")) {
            for (CustomerOrder co : list) {
                Date date2 = co.getOrderDate();
                int year2 = date2.getYear();
                int month2 = date2.getMonth();
                int day2 = date2.getDate();

                if (year1 == year2 && month1 == month2 && day1 == day2) {
                    resultList.add(co);
                }
            }
        } else if (optionDateType.equals("WEEK")) {
            for (CustomerOrder co : list) {
                Date date2 = co.getOrderDate();

                if (!date1.before(date2) && getDateDiff(date1, date2, TimeUnit.DAYS) <= 7) {
                    resultList.add(co);
                }
            }
        } else if (optionDateType.equals("MONTH")) {
            for (CustomerOrder co : list) {
                Date date2 = co.getOrderDate();
                int year2 = date2.getYear();
                int month2 = date2.getMonth();

                if (year1 == year2 && month1 == month2) {
                    resultList.add(co);
                }
            }
        } else if (optionDateType.equals("YEAR")) {
            for (CustomerOrder co : list) {
                Date date2 = co.getOrderDate();
                int year2 = date2.getYear();

                if (year1 == year2) {
                    resultList.add(co);
                }
            }
        }
        return resultList;
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public Long getSelectedMenuItemId() {
        return selectedMenuItemId;
    }

    public void setSelectedMenuItemId(Long selectedMenuItemId) {
        this.selectedMenuItemId = selectedMenuItemId;
    }

    public MenuItem getSelectedMenuItem() {
        return selectedMenuItem;
    }

    public void setSelectedMenuItem(MenuItem selectedMenuItem) {
        this.selectedMenuItem = selectedMenuItem;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public String getOptionMenuItemsType() {
        return optionMenuItemsType;
    }

    public void setOptionMenuItemsType(String OptionMenusItemType) {
        this.optionMenuItemsType = OptionMenusItemType;
    }

    public String getOptionDateType() {
        return optionDateType;
    }

    public void setOptionDateType(String optionDateType) {
        this.optionDateType = optionDateType;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        System.out.println(selectedDate);
        this.selectedDate = selectedDate;
    }

    public Date getTodaysDate() {
        return new Date();
    }

    public BarChartModel getAllMenuItemsBarChart() {
        return allMenuItemsBarChart;
    }

    public Boolean getInitialized() {
        System.out.println(initialized);
        return initialized;
    }

}
