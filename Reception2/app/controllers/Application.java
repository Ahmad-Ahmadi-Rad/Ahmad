package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import models.Patient;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.stm.Ref.View;
import views.html.helper.form;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

public class Application extends Controller {

	private static Form<Patient> patientForm = Form.form(Patient.class);

	public static Result index() {
		if (Patient.find.all() == null) {
			return ok("مشخصات بيمار در حافظه موجود نيست");
		} else {
			return ok(views.html.reception.patient_list.render(Patient.find
					.all()));
		}
	}

	public static Result search() {
		DynamicForm form = form().bindFromRequest();

		String type = form.get("type");
		String key = form.get("key");

		List<Patient> list = new ArrayList<Patient>();

		if (type == null || key == null)
			list = Patient.find.all();
		else if (type.equals("phone")) {
			String sql = " select per.id, per.name, per.last_name "
					+ " from person_model per"
					+ " join phone_model pho on per.id = pho.person_model_id ";
			RawSql rawSql = RawSqlBuilder.parse(sql)
					.columnMapping("per.id", "id")
					.columnMapping("per.name", "name")
					.columnMapping("per.last_name", "lastName").create();
			Query<Patient> query = Ebean.find(Patient.class);
			query.setRawSql(rawSql).where().like("pho.phone", "%" + key + "%");
			list = query.findList();
		} else {
			list = Patient.find.where().like(type, "%" + key + "%").findList();
		}

		return ok(views.html.reception.patient_search.render(list, type, key));
	}

	public static Result update(Integer id) {
		Form<Patient> filledForm = form(Patient.class).fill(
				Patient.find.byId(id));
		return ok(views.html.reception.editForm.render(id, filledForm));

	}

	public static Result saveUpdate() {
		Form<Patient> filledForm = form(Patient.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.reception.editForm.render(
					filledForm.get().id, filledForm));
		} else {
			filledForm.get().save();
			return redirect(routes.Application.index());
		}
	}

	public static Result create() {
		Patient p = new Patient();
		List<Object> ids = Patient.find.findIds();

		int max = 0;
		for (Object id : ids)
			max = Math.max((Integer) id, max);

		max = max + 1;

		p.id = max;

		System.out.println("===============================");
		System.out.println(p.id);
		System.out.println("===============================");

		patientForm = patientForm.fill(p);

		System.out.println(patientForm);

		return ok(views.html.reception.newForm.render(patientForm));
	}

	public static Result saveCreate() {
		Form<Patient> filledForm = form(Patient.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.reception.editForm.render(
					filledForm.get().id, filledForm));
		} else {
			filledForm.get().save();
			return redirect(routes.Application.index());
		}
	}
	
	public static Result printList(){
		
			return ok(views.html.reception.print_list.render(Patient.find.all()));
		
	}
	
	public static Result printReciept(){
		Form<Patient> filledForm = form(Patient.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.reception.editForm.render(
					filledForm.get().id, filledForm));
		} else {
					return ok(views.html.reception.print_reciept.render(filledForm.get()));
		}
		
	}
	

	public static Result delete(Integer id) {
		Patient.find.ref(id).delete();
		return redirect(routes.Application.index());
	}

}