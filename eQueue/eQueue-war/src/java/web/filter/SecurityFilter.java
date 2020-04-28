package web.filter;

import entity.Employee;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import util.enumeration.EmployeeRoleEnum;

@WebFilter(filterName = "SecurityFilter", urlPatterns = {"/*"})
public class SecurityFilter implements Filter {

    private FilterConfig filterConfig;

    private static final String CONTEXT_ROOT = "/eQueue-war/";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpSession httpSession = httpServletRequest.getSession(true);
        String requestServletPath = httpServletRequest.getServletPath();

        if (httpSession.getAttribute("employeeIsLogin") == null) {
            httpSession.setAttribute("employeeIsLogin", false);
        }

        Boolean employeeIsLogin = (Boolean) httpSession.getAttribute("employeeIsLogin");

//        chain.doFilter(request, response);
/* Commented out the check below to ease the development process by excluding all the necessary checks */
        if (!excludeLoginCheck(requestServletPath)) {
            if (employeeIsLogin == true) {
                Employee currentEmployee = (Employee) httpSession.getAttribute("currentEmployee");

                if (checkAccessRight(requestServletPath, currentEmployee.getEmployeeRole())) {
                    chain.doFilter(request, response);
                } else {
                    httpServletResponse.sendRedirect(CONTEXT_ROOT + "accessRightError.xhtml");
                }
            } else {
                httpServletResponse.sendRedirect(CONTEXT_ROOT + "index.xhtml");
            }
        } else {
            chain.doFilter(request, response);
        }

    }

    private Boolean checkAccessRight(String path, EmployeeRoleEnum employeeRole) {
        if (path.equals("/homepage.xhtml")) {
            return true;
        }
        switch (employeeRole) {
            case CASHIER:
                // for transaction management
                return true;
            case MANAGER:
                // for menu, employee, store, order, table, queue and transaction managements
                return true;
//                if (path.equals("/diningTableManagement.xhtml")
//                        ||path.equals("/storeDetails.xhtml")
//                        || path.equals("queueManagement.xhtml")
//                        || path.equals("/createNewEmployee.xhtml")
//                        || path.equals("/deleteEmployee.xhtml")
//                        || path.equals("/updateEmployee.xhtml")
//                        || path.equals("/viewAllEmployees.xhtml")
//                        || path.equals("/viewEmployeeDetails.xhtml")) {
//                    return true;
//                }
            case DEFAULT:
                return true;
        }
        return false;
    }

    private Boolean excludeLoginCheck(String path) {
        return path.equals("/index.xhtml")
                || path.startsWith("/javax.faces.resource");
    }

    @Override
    public void destroy() {

    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("SecurityFilter()");
        }
        StringBuffer sb = new StringBuffer("SecurityFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
