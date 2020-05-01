package jsf.managedBean;

import ejb.session.stateless.CustomerOrderSessionBeanLocal;
import ejb.session.stateless.MenuItemSessionBeanLocal;
import entity.CustomerOrder;
import entity.MenuItem;
import entity.OrderLineItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
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

    private String[] bgColoursArray;
    private String[] borderColoursArray;
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
    private LineChartModel lineChart;

    public AnalyticsPageManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        bgColoursArray = new String[]{
            "rgba(255, 99, 132, 0.2)",
            "rgba(255, 159, 64, 0.2)",
            "rgba(255, 205, 86, 0.2)",
            "rgba(75, 192, 192, 0.2)",
            "rgba(54, 162, 235, 0.2)",
            "rgba(153, 102, 255, 0.2)",
            "rgba(201, 203, 207, 0.2)"
        };

        borderColoursArray = new String[]{
            "rgb(255, 99, 132)",
            "rgb(255, 159, 64)",
            "rgb(255, 205, 86)",
            "rgb(75, 192, 192)",
            "rgb(54, 162, 235)",
            "rgb(153, 102, 255)",
            "rgb(201, 203, 207)"
        };

        initialized = false;
        allMenuItemsBarChart = new BarChartModel();
        optionType = "MENU_ITEMS";
        optionMenuItemsType = "ALL";
        optionDateType = "DAY";
        selectedDate = new Date();
        menuItems = menuItemSessionBean.retrieveAllMenuItems();
        customerOrders = customerOrderSessionBean.retrieveAllOrders();

        this.selectedMenuItemId = null;
    }

    public void processFilter() {

        initialized = true;
        if (optionType.equals("MENU_ITEMS")) {
            if (optionMenuItemsType.equals("ALL")) {
                initAllMenuItemsBarChart();
            } else if (optionMenuItemsType.equals("SINGLE")) {
                try {
                    selectedMenuItem = menuItemSessionBean.retrieveMenuItemById(selectedMenuItemId);
                } catch (Exception ex) {

                }
                initSingleMenuItemLineChart();
            }
        } else if (optionType.equals("SALES_REVENUE")) {
            initSalesRevenueLineChart();
        } else if (optionType.equals("POPULAR_TIMINGS")) {
            initPopularLineChart();
        }
    }

    private void initAllMenuItemsBarChart() {
        allMenuItemsBarChart = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Menu Items");

        List<Number> values = new ArrayList<>();
        List<String> bgColor = new ArrayList<>();
        List<String> borderColor = new ArrayList<>();
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

        int counter = 0;
        for (Map.Entry<MenuItem, Integer> entry : map.entrySet()) {
            System.out.println("Added into Series: " + entry.getKey());
            labels.add(entry.getKey().getMenuItemName());
            values.add(entry.getValue());
            bgColor.add(bgColoursArray[(counter % bgColoursArray.length)]);
            borderColor.add(borderColoursArray[(counter % borderColoursArray.length)]);
            counter++;
        }

        barDataSet.setData(values);
        barDataSet.setBackgroundColor(bgColor);
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        data.setLabels(labels);

        //Data
        allMenuItemsBarChart.setData(data);

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
        title.setText("All Menu Items for " + getFormattedDate());
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

        allMenuItemsBarChart.setOptions(options);
    }

    private void initSingleMenuItemLineChart() {
        lineChart = new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        List<CustomerOrder> orders = filterByDateRange();
        Map<Integer, Integer> map = new HashMap<>();

        for (CustomerOrder co : orders) {
            for (OrderLineItem orderLineItem : co.getOrderLineItems()) {
                if (orderLineItem.getMenuItem().getMenuItemId().equals(selectedMenuItemId)) {
                    Date date = co.getOrderDate();
                    int hours = date.getHours();
                    int day = date.getDate();
                    int month = date.getMonth();

                    if (optionDateType.equals("DAY")) {
                        int count = map.containsKey(hours) ? map.get(hours) : 0;
                        map.put(hours, count + Math.toIntExact(orderLineItem.getQuantity()));
                    } else if (optionDateType.equals("WEEK") || optionDateType.equals("MONTH")) {
                        int count = map.containsKey(day) ? map.get(day) : 0;
                        map.put(day, count + Math.toIntExact(orderLineItem.getQuantity()));
                    } else if (optionDateType.equals("YEAR")) {
                        int count = map.containsKey(month) ? map.get(month) : 0;
                        map.put(month, count + Math.toIntExact(orderLineItem.getQuantity()));
                    }
                }
            }
        }

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println("Added into Series: " + entry.getKey() + ", " + entry.getValue());
            values.add(entry.getValue());
            labels.add(entry.getKey().toString());
        }

        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel(selectedMenuItem.getMenuItemName());
        dataSet.setBorderColor("rgb(75, 192, 192)");
        dataSet.setLineTension(0.1);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Single Menu Item for " + selectedMenuItem.getMenuItemName() + " for " + getFormattedDate());
        options.setTitle(title);

        lineChart.setOptions(options);
        lineChart.setData(data);
    }

    private void initSalesRevenueLineChart() {
        lineChart = new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        List<CustomerOrder> orders = filterByDateRange();
        Map<Integer, Double> map = new HashMap<>();

        for (CustomerOrder co : orders) {
            Date date = co.getOrderDate();
            int hours = date.getHours();
            int day = date.getDate();
            int month = date.getMonth();

            if (optionDateType.equals("DAY")) {
                double count = map.containsKey(hours) ? map.get(hours) : 0;
                map.put(hours, count + co.getTotalAmount());
            } else if (optionDateType.equals("WEEK") || optionDateType.equals("MONTH")) {
                double count = map.containsKey(day) ? map.get(day) : 0;
                map.put(day, count + co.getTotalAmount());
            } else if (optionDateType.equals("YEAR")) {
                double count = map.containsKey(month) ? map.get(month) : 0;
                map.put(month, count + co.getTotalAmount());
            }
        }

        for (Map.Entry<Integer, Double> entry : map.entrySet()) {
            System.out.println("Added into Series: " + entry.getKey() + ", " + entry.getValue());
            values.add(entry.getValue());
            labels.add(entry.getKey().toString());
        }

        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel("Price ($)");
        dataSet.setBorderColor("rgb(54, 162, 235)");
        dataSet.setLineTension(0.1);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Sales Revenue for " + getFormattedDate());
        options.setTitle(title);

        lineChart.setOptions(options);
        lineChart.setData(data);
    }

    private void initPopularLineChart() {
        lineChart = new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Number> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        List<CustomerOrder> orders = filterByDateRange();
        Map<Integer, Integer> map = new HashMap<>();

        for (CustomerOrder co : orders) {
            Date date = co.getOrderDate();
            int hours = date.getHours();
            int day = date.getDate();
            int month = date.getMonth();

            if (optionDateType.equals("DAY")) {
                int count = map.containsKey(hours) ? map.get(hours) : 0;
                map.put(hours, count + 1);
            } else if (optionDateType.equals("WEEK") || optionDateType.equals("MONTH")) {
                int count = map.containsKey(day) ? map.get(day) : 0;
                map.put(day, count + 1);
            } else if (optionDateType.equals("YEAR")) {
                int count = map.containsKey(month) ? map.get(month) : 0;
                map.put(month, count + 1);
            }
        }

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println("Added into Series: " + entry.getKey() + ", " + entry.getValue());
            values.add(entry.getValue());
            labels.add(entry.getKey().toString());
        }

        dataSet.setData(values);
        dataSet.setFill(false);
        dataSet.setLabel("Qty of Orders");
        dataSet.setBorderColor("rgb(255, 99, 132)");
        dataSet.setLineTension(0.1);
        data.addChartDataSet(dataSet);
        data.setLabels(labels);
        LineChartOptions options = new LineChartOptions();
        Title title = new Title();
        title.setDisplay(true);
        title.setText("Popular Timings for " + getFormattedDate());
        options.setTitle(title);

        lineChart.setOptions(options);
        lineChart.setData(data);
    }

    private String getFormattedDate() {
        SimpleDateFormat sdf;
        if (optionDateType.equals("DAY")) {
            sdf = new SimpleDateFormat("d MMM yyyy, EEE");
            return sdf.format(selectedDate);
        } else if (optionDateType.equals("WEEK")) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            cal.add(Calendar.DATE, -6);
            Date dateBefore7Days = cal.getTime();
            sdf = new SimpleDateFormat("d MMM yyyy, EEE");
            return sdf.format(dateBefore7Days) + " to " + sdf.format(selectedDate);
        } else if (optionDateType.equals("MONTH")) {
            sdf = new SimpleDateFormat("MMM yyyy");
            return sdf.format(selectedDate);
        } else if (optionDateType.equals("YEAR")) {
            sdf = new SimpleDateFormat("yyyy");
            return sdf.format(selectedDate);
        }
        return "";
    }

    private List<CustomerOrder> filterByDateRange() {
        Date date1 = selectedDate;
        System.out.println(selectedDate);
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
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);
            cal.add(Calendar.DATE, 1);
            date1 = cal.getTime();
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
        this.selectedDate = selectedDate;
    }

    public Date getTodaysDate() {
        return new Date();
    }

    public BarChartModel getAllMenuItemsBarChart() {
        return allMenuItemsBarChart;
    }

    public LineChartModel getLineChart() {
        return lineChart;
    }

    public Boolean getInitialized() {
        return initialized;
    }
}
