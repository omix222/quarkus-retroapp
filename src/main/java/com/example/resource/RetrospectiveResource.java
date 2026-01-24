package com.example.resource;

import com.example.entity.Retrospective;
import com.example.repository.RetrospectiveRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * レトロスペクティブREST API
 */
@Path("/api/retrospectives")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RetrospectiveResource {

    @Inject
    RetrospectiveRepository retrospectiveRepository;

    /**
     * すべてのレトロスペクティブを取得
     */
    @GET
    public List<Retrospective> getAll() {
        return retrospectiveRepository.listAll();
    }

    /**
     * 特定のレトロスペクティブを取得
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Retrospective retrospective = retrospectiveRepository.findById(id);
        if (retrospective == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(retrospective).build();
    }

    /**
     * 最新のレトロスペクティブを取得
     */
    @GET
    @Path("/recent")
    public List<Retrospective> getRecent(@QueryParam("limit") @DefaultValue("10") int limit) {
        return retrospectiveRepository.findRecent(limit);
    }

    /**
     * 新しいレトロスペクティブを作成
     */
    @POST
    @Transactional
    public Response create(Retrospective retrospective) {
        retrospectiveRepository.persist(retrospective);
        return Response.status(Response.Status.CREATED).entity(retrospective).build();
    }

    /**
     * レトロスペクティブを更新
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Retrospective updatedRetrospective) {
        Retrospective retrospective = retrospectiveRepository.findById(id);
        if (retrospective == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        retrospective.title = updatedRetrospective.title;
        retrospective.date = updatedRetrospective.date;
        retrospective.description = updatedRetrospective.description;

        return Response.ok(retrospective).build();
    }

    /**
     * レトロスペクティブを削除
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = retrospectiveRepository.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
