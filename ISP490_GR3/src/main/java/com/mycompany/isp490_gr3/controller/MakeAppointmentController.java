//package com.mycompany.isp490_gr3.controller;
//
//import com.mycompany.isp490_gr3.dao.DAODoctor;
//import com.mycompany.isp490_gr3.model.Doctor;
//import jakarta.servlet.ServletException;
//// KHÔNG CÓ @WebServlet annotation ở đây
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.List;
//
//// Đảm bảo rằng servlet này được ánh xạ tới /makeappointments trong web.xml của bạn
//public class MakeAppointmentController extends HttpServlet {
//    private static final long serialVersionUID = 1L;
//    private DAODoctor DAOdoctor;
//
//    @Override
//    public void init() throws ServletException {
//        super.init();
//        DAOdoctor = new DAODoctor();
//    }
//     
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // Lấy 5 bác sĩ nổi bật để hiển thị trên trang make-appointment.jsp
//        List<Doctor> doctors = DAOdoctor.findAllDoctors(5, 0);
//        request.setAttribute("doctors", doctors);
//        
//        // Chuyển tiếp đến JSP. Lưu ý đường dẫn này phải khớp với vị trí JSP của bạn.
//        // Nếu make-appointment.jsp nằm trực tiếp trong thư mục WEB-INF/jsp/, thì là "/jsp/make-appointment.jsp"
//        request.getRequestDispatcher("/jsp/make-appointment.jsp").forward(request, response);
//    }
//}