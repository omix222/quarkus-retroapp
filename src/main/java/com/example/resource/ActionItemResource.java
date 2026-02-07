package com.example.resource;

import com.example.entity.ActionItem;
import com.example.entity.ActionItemStatus;
import com.example.entity.Retrospective;
import com.example.repository.ActionItemRepository;
import com.example.repository.RetrospectiveRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * アクションアイテムREST API
 */
@Path("/api/action-items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActionItemResource {

    @Inject
    ActionItemRepository actionItemRepository;

    @Inject
    RetrospectiveRepository retrospectiveRepository;

    /**
     * レトロスペクティブのアクションアイテムを取得
     */
    @GET
    @Path("/retrospectives/{id}")
    public Response getActionItemsByRetrospective(@PathParam("id") Long retrospectiveId) {
        List<ActionItem> actionItems = actionItemRepository.findByRetrospectiveId(retrospectiveId);
        return Response.ok(actionItems).build();
    }

    /**
     * ステータスでアクションアイテムを取得
     */
    @GET
    @Path("/retrospectives/{id}/status/{status}")
    public Response getActionItemsByStatus(
            @PathParam("id") Long retrospectiveId,
            @PathParam("status") ActionItemStatus status) {
        List<ActionItem> actionItems = actionItemRepository.findByRetrospectiveIdAndStatus(retrospectiveId, status);
        return Response.ok(actionItems).build();
    }

    /**
     * 担当者でアクションアイテムを取得
     */
    @GET
    @Path("/assignee/{assignee}")
    public Response getActionItemsByAssignee(@PathParam("assignee") String assignee) {
        List<ActionItem> actionItems = actionItemRepository.findByAssignee(assignee);
        return Response.ok(actionItems).build();
    }

    /**
     * アクションアイテムを追加
     */
    @POST
    @Path("/retrospectives/{id}")
    @Transactional
    public Response createActionItem(@PathParam("id") Long retrospectiveId, @Valid ActionItem actionItem) {
        Retrospective retrospective = retrospectiveRepository.findById(retrospectiveId);
        if (retrospective == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        actionItem.retrospective = retrospective;
        actionItemRepository.persist(actionItem);

        return Response.status(Response.Status.CREATED).entity(actionItem).build();
    }

    /**
     * アクションアイテムを更新
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateActionItem(@PathParam("id") Long id, @Valid ActionItem updatedActionItem) {
        ActionItem actionItem = actionItemRepository.findById(id);
        if (actionItem == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        actionItem.description = updatedActionItem.description;
        actionItem.assignee = updatedActionItem.assignee;
        actionItem.jiraTicket = updatedActionItem.jiraTicket;
        actionItem.status = updatedActionItem.status;

        return Response.ok(actionItem).build();
    }

    /**
     * アクションアイテムのステータスを更新
     */
    @PATCH
    @Path("/{id}/status")
    @Transactional
    public Response updateActionItemStatus(
            @PathParam("id") Long id,
            @QueryParam("status") ActionItemStatus status) {
        ActionItem actionItem = actionItemRepository.findById(id);
        if (actionItem == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        actionItem.updateStatus(status);

        return Response.ok(actionItem).build();
    }

    /**
     * アクションアイテムを削除
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteActionItem(@PathParam("id") Long id) {
        boolean deleted = actionItemRepository.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
