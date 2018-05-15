package controllers;

import models.Constants;
import models.User;
import play.mvc.*;

import java.util.List;

public class Application extends Controller {

    private static void checkTeacher(){
        checkUser();

        User u = (User) renderArgs.get("user");
        if (!u.getType().equals(Constants.User.TEACHER)){
            return;
        }
    }

    private static void checkUser(){
        if (session.contains("username")){
            User u = User.loadUser(session.get("username"));
            if (u != null){
                renderArgs.put("user", u);
                return;
            }
        }
        Secure.login();
    }

    public static void index() {
        checkUser();

        User u = (User) renderArgs.get("user");

        if (u.getType().equals(Constants.User.TEACHER)){
            List<User> students = User.loadStudents();
            render("Application/teacher.html", u, students);
        }else{
            render("Application/student.html", u);
        }
    }


    public static void removeStudent(String student) {
        checkTeacher();

        User.remove(student);
        index();
    }


    public static void setMark(String student) {
        User u = User.loadUser(student);
        render(u);
    }

    public static void doSetMark(String student, Integer mark) {
        User u = User.loadUser(student);
	try{	
		if (mark>0 && mark <=10){
			u.setMark(mark);
		}
	else{
		//Show message: mark must be in range {0,10}
		render("PublicContentBase/markError.html");
	}
	}catch (NullPointerException exception){
		//Manage the exceptions and show error message for example
		render("PublicContentBase/markError.html");
	}catch (Exception exception){
		//Show other exceptions error
	}
        u.save();
        index();
    }
}
