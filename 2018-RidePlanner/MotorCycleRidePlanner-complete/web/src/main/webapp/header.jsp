<%-- 
    Document   : header.jsp
    Created on : Feb 27, 2019, 7:07:29 PM
    Author     : cgallen
--%>

<%@page import="org.solent.com600.example.journeyplanner.model.Role"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String logout = (String) request.getParameter("logout");
    if (logout != null) {
        session.setAttribute("sessionUserRole", Role.ANONYMOUS);
        session.setAttribute("sessionUserName", "anonymous");
        response.sendRedirect("./mainPage.jsp");
    }

    String sessionUserName = (String) session.getAttribute("sessionUserName");
    if (sessionUserName == null) {
        sessionUserName = "anonymous";
        session.setAttribute("sessionUserName", sessionUserName);
    }

    Role sessionUserRole = (Role) session.getAttribute("sessionUserRole");
    if (sessionUserRole == null) {
        sessionUserRole = Role.ANONYMOUS;
        session.setAttribute("sessionUserRole", sessionUserRole);
    }

    String tabSelected = (String) request.getParameter("tabSelected");

%>
<!-- content from header.jsp -->
<head>
    <title>Motorcycle Club Main</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" href="./css/andreas08.css" type="text/css" />
    <link rel="stylesheet" href="./css/accordianStyle.css" type="text/css" />
    <link rel="stylesheet" href="./css/style.css" type="text/css" />



    <meta name="description" content="Motorcycle website" />
    <meta name="keywords" content="motorcycle ride tour" />

    <!-- opengraph protocol -->
    <meta property="og:type" content="website" />
    <meta property="og:title" content="motorcycle rideout" />
    <meta property="og:description" content="Rideout site" />

    <!-- this is to drive time picker -->
    <link rel="stylesheet" type="text/css" href="js/timepicker.min.css">
    <script src="js/timepicker.min.js"></script>
    <script>
        document.addEventListener("DOMContentLoaded", function (event) {
            timepicker.load({
                interval: 1,
                defaultHour: 0
            });
        });
    </script>
</head>
<body>
    <div id="container">
        <div id="header" >
            <table style="border-collapse: collapse; border: none; ">
                <tr>
                    <td style="border-collapse: collapse; border: none; ">
                        <h1 style="text-align: justify;">Motor Cycle Rideout</h1>
                        <h2>Rides with a difference...</h2>
                    </td>
                    <td style="border-collapse: collapse; border: none; ">
                        <div style="text-align: right;">
                            <% if (Role.ANONYMOUS.equals(sessionUserRole)) {
                            %>
                            <h2>Not Logged In</h2>
                            <form action="login.jsp" method="post">
                                <input type="submit" value="Login">
                            </form>
                            <%
                            } else {
                            %>
                            <h2>Username: <%=sessionUserName%></h2>
                            <h2>Role    : <%=sessionUserRole%></h2>
                            <form action="mainPage.jsp" method="post">
                                <input type="hidden" name="logout" value="logout">
                                <input type="submit" value="Log Out">
                            </form>
                            <%
                                }
                            %>
                        </div>
                    </td>
                </tr>
            </table>


        </div>
        <div id="navigation">
            <ul>
                <li <%=("Main".equals(tabSelected) ? "class=\"selected\"" : "")%> >
                    <a href="./mainPage.jsp?tabSelected=Main">Main</a></li>

                <% if (!Role.ANONYMOUS.equals(sessionUserRole)) {%>
                <li <%=("MyProfile".equals(tabSelected) ? "class=\"selected\"" : "")%> >
                    <a href="./userInfo.jsp?tabSelected=MyProfile&action=myProfile&selUserName=<%=sessionUserName%>">My Profile</a></li>
                <li <%=("ManageRideouts".equals(tabSelected) ? "class=\"selected\"" : "")%> >
                    <a href="./listRideouts.jsp?tabSelected=ManageRideouts">Manage Rideouts</a></li>

                <% if (Role.ADMIN.equals(sessionUserRole)) {%>
                <li <%=("ManageUsers".equals(tabSelected) ? "class=\"selected\"" : "")%> > 
                    <a href="./listUsers.jsp?tabSelected=ManageUsers">Manage Users</a></li>

                <% }%>
                <% }%>
                <li <%=("About".equals(tabSelected) ? "class=\"selected\"" : "")%> >
                    <a href="./about.jsp?tabSelected=About">About</a></li>
            </ul>
        </div>
        <!-- end of  content from header.jsp -->
        <!-- main JSP content will go here -->
