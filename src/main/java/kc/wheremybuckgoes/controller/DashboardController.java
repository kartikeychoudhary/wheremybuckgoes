/**
 * Controller for handling dashboard and visualization operations.
 *
 * This controller provides endpoints for retrieving dashboard data, transaction summaries,
 * and managing visualization settings. It serves as the central component for the data
 * visualization features of the WhereMyBuckGoes expense tracking application.
 *
 * @author Kartikey Choudhary (kartikey31choudhary@gmail.com)
 * @version 1.0
 * @since 2024
 */
package kc.wheremybuckgoes.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kc.wheremybuckgoes.dto.TransactionDTO;
import kc.wheremybuckgoes.dto.VisualizeDTO;
import kc.wheremybuckgoes.modal.*;
import kc.wheremybuckgoes.modal.card.Card;
import kc.wheremybuckgoes.response.GenericResponse;
import kc.wheremybuckgoes.services.DashboardService;
import kc.wheremybuckgoes.services.TransactionService;
import kc.wheremybuckgoes.services.VisualizeService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static kc.wheremybuckgoes.utils.ApplicationHelper.mapToGenericResponse;

@RestController
@RequestMapping("api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    /**
     * Service for transaction operations.
     * Provides methods to retrieve and process transaction data.
     */
    private final TransactionService transactionService;
    /**
     * Service for dashboard operations.
     * Handles aggregation and preparation of dashboard summary data.
     */
    private final DashboardService dashboardService;
    /**
     * Service for visualization operations.
     * Manages user-specific visualization settings and chart generation.
     */
    private final VisualizeService visualizeService;

    /**
     * Retrieves dashboard data for transactions after a specified date.
     * <p>
     * This endpoint aggregates transaction data into dashboard metrics including
     * spending summaries, category breakdowns, and trend analysis. The date parameter
     * can be a specific timestamp or "all" to include all transactions.
     *
     * @param date String representation of a timestamp or "all" for all transactions
     * @return ResponseEntity containing:
     *         - 200 OK with Dashboard object containing aggregated metrics
     */
    @GetMapping(path = "/after/{date}")
    public ResponseEntity<GenericResponse<Dashboard>> getTransactionsAfterDate(@PathVariable("date") String date) {
        long d = 0;
        if(!date.equalsIgnoreCase("all")){
            d = Long.parseLong(date);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Dashboard dashboard = dashboardService.getDashboardAfterDate(user, d);
        GenericResponse<Dashboard> gr = mapToGenericResponse(HttpStatus.OK, dashboard);
        return ResponseEntity.ok().body(gr);
    }

    /**
     * Retrieves all transactions for the authenticated user.
     *
     * This endpoint returns a complete list of non-deleted transactions
     * associated with the current user, converted to DTO format for API consumption.
     *
     * @return ResponseEntity containing:
     *         - 200 OK with list of TransactionDTO objects
     */
    @GetMapping
    public ResponseEntity<GenericResponse<List<TransactionDTO>>> getTransactionsForDate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Transaction> transactions = transactionService.getAllTransactionForUser(user.getEmail());
        GenericResponse<List<TransactionDTO>> gr = mapToGenericResponse(HttpStatus.OK, transactions.stream().filter(transaction -> !transaction.isDeleted()).map(Transaction::convertToDTO).toList());
        return ResponseEntity.ok().body(gr);
    }

    /**
     * Generates a preview of chart data based on provided card configuration.
     * <p>
     * This endpoint processes a card configuration to generate sample chart data
     * without saving the configuration. It allows users to preview how their
     * visualization will appear before committing to the changes.
     *
     * @param cardDTO JSON string representation of a Card configuration
     * @return ResponseEntity containing:
     *         - 200 OK with JSON string of chart data
     *         - 400 Bad Request if the card configuration cannot be parsed
     */
    @PostMapping(path = "/visualize/preview")
    public ResponseEntity<GenericResponse<String>> getChartPreview(@RequestBody String cardDTO) {
        ObjectMapper mapper = new ObjectMapper();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        try {
            Card card = mapper.readValue(cardDTO, Card.class);
            JSONObject result = visualizeService.getChartPreview(user, card);
            return ResponseEntity.ok().body(mapToGenericResponse(HttpStatus.OK, result.toString(1)));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves the current visualization settings for the authenticated user.
     * <p>
     * This endpoint returns the user's saved dashboard configuration including
     * card layouts, chart types, and filter settings.
     *
     * @return ResponseEntity containing:
     *         - 200 OK with VisualizeDTO containing dashboard configuration
     */
    @GetMapping("/visualize")
    public ResponseEntity<GenericResponse<VisualizeDTO>>  getDashboard(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Visualize visualize = visualizeService.getSettingByUser(user);
        GenericResponse<VisualizeDTO> gr = mapToGenericResponse(HttpStatus.OK, visualize.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }

    /**
     * Updates the visualization settings for the authenticated user.
     * <p>
     * This endpoint saves changes to the user's dashboard configuration,
     * including modifications to card layouts, chart types, and filter settings.
     *
     * @param settingsDTO VisualizeDTO containing the updated dashboard configuration
     * @return ResponseEntity containing:
     *         - 200 OK with updated VisualizeDTO
     *         - 404 Not Found if the user has no existing visualization settings
     */
    @PostMapping("/visualize")
    public ResponseEntity<GenericResponse<VisualizeDTO>>  updateSettings(@RequestBody VisualizeDTO settingsDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Visualize visualize = visualizeService.getSettingByUser(user);
        if(visualize == null){
            return ResponseEntity.notFound().build();
        }
        Visualize updatedDashboard = visualizeService.createOrUpdateVisualize(Visualize.builder().createdBy(user).visualizeId(visualize.getVisualizeId()).dashboard(settingsDTO.getDashboard().getBytes()).build());
        GenericResponse<VisualizeDTO> gr = mapToGenericResponse(HttpStatus.OK, updatedDashboard.convertToDTO());
        return ResponseEntity.ok().body(gr);
    }
}
