package com.example.resource;

import com.example.models.*;
import com.example.repository.*;
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

@Path("/department")
@Produces(MediaType.APPLICATION_JSON)   //указывает, что методы ресурса будут возвращать данные в формате JSON
@Consumes(MediaType.APPLICATION_JSON)  //указывает, что методы ресурса будут принимать данные в формате JSON
public class DepartmentResource {

    @Inject
    DepartmentRepository departmentRepository;


    @GET
    @Path("/pag")
    public Response getDepartmentByPagination(
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
            List<Department> departments = Department.findAll(sort).page(page).list();

            // обработка результатов и создания ответа
            long totalCount = Department.count();
            int totalPages = (int) totalCount / pageSize;

            // создаем объект, содержащий информацию о пагинации и список сотрудников
            PaginationResult<Department> result = new PaginationResult<>(
                    pageNumber, pageSize, totalPages, totalCount, departments);

            // возвращаем ответ в формате JSON
            return Response.ok(result).build();
        } catch (Exception e) {
            // Обработка ошибки и создание пользовательского сообщения
            String errorMessage = "Something wrong:";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorMessage)
                    .build();
        }
    }

        @GET
        public List<Department> getAllDepartments() {
            return Department.listAll();
        }

        @GET
        @Path("/{id}")
        public Department get(@PathParam("id") Long id) {
            System.out.println(Department.findById(id));
            return Department.findById(id);
        }

    @GET
        @Path("/search")
        public Department findByDepartment(@QueryParam("name") String name) {
        return departmentRepository.findByDepartment(name);
    }

        @POST
        @Transactional
        public void createDepartment(@Valid Department department){

            if (department.name == null) {
                throw new BadRequestException("Department name can't be null!");
            }
            if (department.name.length() > 30 || department.name.length() <= 2) {
                throw new BadRequestException("Department name can't be less 2 symbols or more 30 symbols!");
            }department.persist();
        }

        @PUT
        @Path("/{id}")
        @Transactional
        public void updateDepartment(@Valid @PathParam("id") Long id, Department department) {
            Department entity = Department.findById(id);
            if (department != null && department.name.length() < 30 && department.name.length() > 1 ) {
                entity.name = department.name;
            } else {
                throw new BadRequestException("Department name can't be less 2 symbols or more 30 symbols!");
            }
        }
        @DELETE
        @Path("/{id}")
        @Transactional
        public void deleteDepartment(@PathParam("id") Long id) {
            Department entity = Department.findById(id);
            if (entity != null) {
                entity.delete();
            }
        }
}
