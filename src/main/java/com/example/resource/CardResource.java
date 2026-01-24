package com.example.resource;

import com.example.entity.Card;
import com.example.entity.CardType;
import com.example.entity.Retrospective;
import com.example.repository.CardRepository;
import com.example.repository.RetrospectiveRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * カードREST API
 */
@Path("/api/cards")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CardResource {

    @Inject
    CardRepository cardRepository;

    @Inject
    RetrospectiveRepository retrospectiveRepository;

    /**
     * レトロスペクティブのカードを取得
     */
    @GET
    @Path("/retrospectives/{id}")
    public Response getCardsByRetrospective(@PathParam("id") Long retrospectiveId) {
        List<Card> cards = cardRepository.findByRetrospectiveId(retrospectiveId);
        return Response.ok(cards).build();
    }

    /**
     * レトロスペクティブのカードをタイプで取得
     */
    @GET
    @Path("/retrospectives/{id}/type/{type}")
    public Response getCardsByType(
            @PathParam("id") Long retrospectiveId,
            @PathParam("type") CardType type) {
        List<Card> cards = cardRepository.findByRetrospectiveIdAndType(retrospectiveId, type);
        return Response.ok(cards).build();
    }

    /**
     * 投票数の多いカードを取得
     */
    @GET
    @Path("/retrospectives/{id}/top-voted")
    public Response getTopVotedCards(
            @PathParam("id") Long retrospectiveId,
            @QueryParam("limit") @DefaultValue("5") int limit) {
        List<Card> cards = cardRepository.findTopVoted(retrospectiveId, limit);
        return Response.ok(cards).build();
    }

    /**
     * カードを追加
     */
    @POST
    @Path("/retrospectives/{id}")
    @Transactional
    public Response createCard(@PathParam("id") Long retrospectiveId, Card card) {
        Retrospective retrospective = retrospectiveRepository.findById(retrospectiveId);
        if (retrospective == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        card.retrospective = retrospective;
        cardRepository.persist(card);

        return Response.status(Response.Status.CREATED).entity(card).build();
    }

    /**
     * カードを更新
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateCard(@PathParam("id") Long id, Card updatedCard) {
        Card card = cardRepository.findById(id);
        if (card == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        card.type = updatedCard.type;
        card.content = updatedCard.content;

        return Response.ok(card).build();
    }

    /**
     * カードに投票
     */
    @POST
    @Path("/{id}/vote")
    @Transactional
    public Response voteCard(@PathParam("id") Long id) {
        Card card = cardRepository.findById(id);
        if (card == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        card.incrementVote();

        return Response.ok(card).build();
    }

    /**
     * カードの投票を取り消し
     */
    @POST
    @Path("/{id}/unvote")
    @Transactional
    public Response unvoteCard(@PathParam("id") Long id) {
        Card card = cardRepository.findById(id);
        if (card == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        card.decrementVote();

        return Response.ok(card).build();
    }

    /**
     * カードを削除
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteCard(@PathParam("id") Long id) {
        boolean deleted = cardRepository.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
