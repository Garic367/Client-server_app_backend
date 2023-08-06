package com.example.resource;

import com.example.models.Department;
import com.example.models.Employee;
import com.example.models.PaginationResult;
import com.example.repository.EmployeeRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


import java.util.List;
import java.util.Objects;


@Path("/employee")
@Produces(MediaType.APPLICATION_JSON)   //указывает, что методы ресурса будут возвращать данные в формате JSON
@Consumes(MediaType.APPLICATION_JSON)  //указывает, что методы ресурса будут принимать данные в формате JSON
public class EmployeeResource {

    @Inject
    EmployeeRepository employeeRepository;

    @GET
    public List<Employee> getAllEmployees() {
        return Employee.listAll();
    }

    @GET
    @Path("/pag")
    public Response getEmployeesByPagination(
            @QueryParam("pageNumber") @DefaultValue("1") int pageNumber,
            @QueryParam("pageSize") int pageSize,
            @QueryParam("sortField") String sortField,
            @QueryParam("sortOrder") String sortOrder) {
        try {

            Sort.Direction direction = Sort.Direction.Ascending;
            if ("descend".equalsIgnoreCase(sortOrder)) {
                direction = Sort.Direction.Descending;
            }

            if (!(Objects.equals(sortField, "id")) || sortField.isEmpty()) {
                sortField = "id";
            }


            Sort sort = Sort.by(sortField, direction);
            Page page = Page.of(pageNumber - 1, pageSize);

            //  получение списка сотрудников с учетом пагинации и сортировки
            List<Employee> employees = Employee.findAll(sort).page(page).list();

            // обработка результатов и создания ответа
            long totalCount = Employee.count();
            int totalPages = (int) totalCount / pageSize;

            // создаем объект, содержащий информацию о пагинации и список сотрудников
            PaginationResult<Employee> result = new PaginationResult<>(
                    pageNumber, pageSize, totalPages, totalCount, employees);

            // возвращаем ответ в формате JSON
            return Response.ok(result).build();

        } catch (Exception e) {
            // Обработка ошибки и создание пользовательского сообщения
            String errorMessage = "Something wrong:";
            System.out.println('e');
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorMessage)
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getEmployeeById(@PathParam("id") Long id) {

        Employee employee = Employee.findById(id);
        if (employee != null) {
            return Response.status(200).entity(employee).build();

        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/search")
    public Employee getEmployeeByName(@QueryParam("name") String name) {
        return employeeRepository.findByName(name);
    }


    @POST
    @Transactional
    public void createEmployee(@Valid Employee employee) {

        Objects.requireNonNull(employee, "Employee must not be null!");
        if (employeeRepository.existsByName(employee.name)) {
            throw new BadRequestException("Such name already exists!");
        }
        if (employee.name == null) {
            throw new BadRequestException("Employee name can't be null!");
        }
        if (employee.name.length() > 20 || employee.name.length() < 3) {
            throw new BadRequestException("Employee name can't be less 3 symbols or more 30 symbols!");
        }
        if (employee.salary < 100 || employee.salary > 1000) {
            throw new BadRequestException("Employee salary must be between 100 and 1000!");
        }
        if (employee.onVacation == null) {
            throw new BadRequestException("Value onVacation must be true or false!");
        }
        if (employee.role == null || !employeeRepository.isValidRole(employee.role)) {
            throw new BadRequestException("Such role doesn't exist!");
        }

        Department department = Department.findById(employee.department.id);
        if (department != null) {
            employee.department = department;
            employee.persist();
        } else {
            throw new BadRequestException("Department not found");
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public void updateEmployee(@Valid @PathParam("id") Long id, Employee employee) {
        Employee entity = Employee.findById(id);
        if (entity != null && employee.name != null && employee.name.length() >= 3 && employee.name.length() < 30 ) {
            if (employee.salary >=100 && employee.salary <=1000) {
                entity.name = employee.name;
                entity.salary = employee.salary;
                entity.role = employee.role;
                entity.onVacation = employee.onVacation;
            }
        }
    }
    @DELETE
    @Path("/{id}")
    @Transactional
    public void deleteEmployee(@PathParam("id") Long id) {
        Employee entity = Employee.findById(id);
        if (entity != null) {
            entity.delete();
        }
    }
}
