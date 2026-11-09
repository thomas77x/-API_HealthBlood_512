/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.lasalle.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.lasalle.controller.ControllerMedida;
import org.lasalle.controller.ControllerUsuario;
import org.lasalle.model.Medida;
import org.lasalle.model.Usuario;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Thomas
 */

@Path("glucosa")

public class RestGlucosa {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Path("saludar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludar(){
        String respuesta = """
                           { "response":"Hola desde Java" }
                           """;
        return Response.ok(respuesta).build();
    }
    
    @Path("saludarPersonalizado")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response saludarPersonalizado(
            @QueryParam("nombre") String nombre
    ){
        String respuesta = jsonMensaje("response", "Hola " + nombre + " desde Java");
        return Response.ok(respuesta).build();
    }

    @Path("getAllUsuarios")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsuarios(){
        ControllerUsuario cu = new ControllerUsuario();
        List<Usuario> lista = new ArrayList<>();
        Gson gson = new Gson();
        String out = "";
        try{
            lista = cu.getUsuarios();
            out = gson.toJson(lista);
        }catch(Exception e){
            e.printStackTrace();
            return Response.serverError().entity("{\"result\": \"Lista Vacia\"}").build();
        }
        return Response.ok(out).build();
    }

    @Path("getUsuarioById")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsuarioById(
            @QueryParam("idUsuario") String idUsuario
    ){
        if(idUsuario == null || idUsuario.isBlank()){
            return badRequest("El idUsuario es obligatorio");
        }

        int idUsuarioInt;
        try{
            idUsuarioInt = Integer.parseInt(idUsuario.trim());
        }catch(NumberFormatException e){
            return badRequest("El idUsuario debe ser un número entero");
        }
        if(idUsuarioInt <= 0){
            return badRequest("El idUsuario debe ser mayor que cero");
        }

        ControllerUsuario cu = new ControllerUsuario();
        Gson gson = new Gson();
        try{
            Usuario usuario = cu.getUsuarioById(idUsuarioInt);
            if(usuario == null){
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(jsonMensaje("result", "Usuario no encontrado"))
                        .build();
            }
            return Response.ok(gson.toJson(usuario)).build();
        }catch(SQLException e){
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"result\":\"Error al obtener el usuario\"}")
                    .build();
        }
    }

    @Path("insertUsuario")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertUsuario(
            @FormParam("nombre") String nombre,
            @FormParam("primerApellido") String primerApellido,
            @FormParam("segundoApellido") String segundoApellido,
            @FormParam("email") String email
    ){
        Gson gson = new Gson();

        String error = validarUsuario(nombre, primerApellido, email);
        if(error != null){
            return badRequest(error);
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setPrimerApellido(primerApellido.trim());
        usuario.setSegundoApellido(
                (segundoApellido == null || segundoApellido.isBlank()) ? null : segundoApellido.trim());
        usuario.setEmail(email.trim());

        try{
            ControllerUsuario cu = new ControllerUsuario();
            int id = cu.insertarUsuario(usuario);
            usuario.setIdUsuario(id);

            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(usuario))
                    .build();
        }catch(SQLException e){
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"result\":\"Error al guardar el usuario\"}")
                    .build();
        }
    }

    private String validarUsuario(String nombre, String primerApellido, String email){
        if(nombre == null || nombre.isBlank()){
            return "El nombre es obligatorio";
        }
        if(primerApellido == null || primerApellido.isBlank()){
            return "El primer apellido es obligatorio";
        }
        if(email == null || email.isBlank()){
            return "El email es obligatorio";
        }
        if(nombre.trim().length() > 40){
            return "El nombre supera 40 caracteres";
        }
        if(primerApellido.trim().length() > 40){
            return "El primer apellido supera 40 caracteres";
        }
        if(email.trim().length() > 50){
            return "El email supera 50 caracteres";
        }
        return null;
    }

    @Path("getAllMedidas")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMedidas(){
        ControllerMedida cm = new ControllerMedida();
        Gson gson = new Gson();
        try{
            return Response.ok(gson.toJson(cm.getMedidas())).build();
        }catch(SQLException e){
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"result\":\"Error al obtener las medidas\"}")
                    .build();
        }
    }

    @Path("getMedidasByUsuario")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedidasByUsuario(
            @QueryParam("idUsuario") String idUsuario
    ){
        if(idUsuario == null || idUsuario.isBlank()){
            return badRequest("El idUsuario es obligatorio");
        }

        int idUsuarioInt;
        try{
            idUsuarioInt = Integer.parseInt(idUsuario.trim());
        }catch(NumberFormatException e){
            return badRequest("El idUsuario debe ser un número entero");
        }
        if(idUsuarioInt <= 0){
            return badRequest("El idUsuario debe ser mayor que cero");
        }

        ControllerMedida cm = new ControllerMedida();
        Gson gson = new Gson();
        try{
            return Response.ok(gson.toJson(cm.getMedidasByUsuario(idUsuarioInt))).build();
        }catch(SQLException e){
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"result\":\"Error al obtener las medidas del usuario\"}")
                    .build();
        }
    }

    @Path("insertMedida")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertMedida(
            @FormParam("idUsuario") String idUsuario,
            @FormParam("glucosa") String glucosa,
            @FormParam("fechaHora") String fechaHora,
            @FormParam("contexto") String contexto,
            @FormParam("notas") String notas
    ){
        Gson gson = new Gson();

        if(idUsuario == null || idUsuario.isBlank()){
            return badRequest("El idUsuario es obligatorio");
        }
        if(glucosa == null || glucosa.isBlank()){
            return badRequest("La glucosa es obligatoria");
        }
        if(contexto != null && contexto.trim().length() > 40){
            return badRequest("El contexto supera 40 caracteres");
        }
        if(notas != null && notas.trim().length() > 200){
            return badRequest("Las notas superan 200 caracteres");
        }

        int idUsuarioInt;
        try{
            idUsuarioInt = Integer.parseInt(idUsuario.trim());
        }catch(NumberFormatException e){
            return badRequest("El idUsuario debe ser un número entero");
        }
        if(idUsuarioInt <= 0){
            return badRequest("El idUsuario debe ser mayor que cero");
        }

        int glucosaInt;
        try{
            glucosaInt = Integer.parseInt(glucosa.trim());
        }catch(NumberFormatException e){
            return badRequest("La glucosa debe ser un número entero");
        }
        if(glucosaInt <= 0){
            return badRequest("La glucosa debe ser mayor que cero");
        }

        String fechaNormalizada;
        if(fechaHora == null || fechaHora.isBlank()){
            fechaNormalizada = LocalDateTime.now().format(FORMATO_FECHA);
        }else{
            try{
                fechaNormalizada = LocalDateTime.parse(fechaHora.trim(), FORMATO_FECHA)
                        .format(FORMATO_FECHA);
            }catch(DateTimeParseException e){
                return badRequest("La fechaHora debe tener el formato yyyy-MM-dd HH:mm:ss");
            }
        }

        Medida medida = new Medida();
        medida.setIdUsuario(idUsuarioInt);
        medida.setGlucosa(glucosaInt);
        medida.setFechaHora(fechaNormalizada);
        medida.setContexto((contexto == null || contexto.isBlank()) ? null : contexto.trim());
        medida.setNotas((notas == null || notas.isBlank()) ? null : notas.trim());

        try{
            ControllerMedida cm = new ControllerMedida();
            medida.setIdMedida(cm.insertarMedida(medida));

            return Response.status(Response.Status.CREATED)
                    .entity(gson.toJson(medida))
                    .build();
        }catch(SQLIntegrityConstraintViolationException e){
            return badRequest("El idUsuario no existe");
        }catch(SQLException e){
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"result\":\"Error al guardar la medida\"}")
                    .build();
        }
    }

    private Response badRequest(String mensaje){
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje("result", mensaje))
                .build();
    }

    private static String jsonMensaje(String clave, String mensaje){
        return new Gson().toJson(Collections.singletonMap(clave, mensaje));
    }
}
