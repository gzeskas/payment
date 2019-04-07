package lt.gzeskas.payment.web.servlet.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lt.gzeskas.payment.domain.TransactionStatus;
import lt.gzeskas.payment.domain.TransferRequest;
import lt.gzeskas.payment.exception.AccountNotFoundException;
import lt.gzeskas.payment.exception.NotEnoughMoneyException;
import lt.gzeskas.payment.service.MoneyTransferService;
import lt.gzeskas.payment.web.servlet.transfer.exception.BadRequestException;
import lt.gzeskas.payment.web.servlet.transfer.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class MoneyTransferServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MoneyTransferServlet.class);
    private static final String supportedContentType = "application/json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MoneyTransferService moneyTransferService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        Object moneyTransferService = config.getServletContext().getAttribute("moneyTransferService");
        if (moneyTransferService == null) {
            throw new IllegalArgumentException("moneyTransferService is not set in servlet context");
        }
        this.moneyTransferService = (MoneyTransferService) config.getServletContext().getAttribute("moneyTransferService");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MoneyTransferApiRequest moneyTransferApiRequest;
        try {
            moneyTransferApiRequest = tryConvertRequestBody(req);
        } catch (BadRequestException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        try {
            TransactionStatus transfer = moneyTransferService.transfer(new TransferRequest(
                            moneyTransferApiRequest.getAccountFrom(),
                            moneyTransferApiRequest.getAccountTo(),
                            moneyTransferApiRequest.getAmount()
                    )
            );
            sendOkResponse(resp, new TransferStatusApiResponse(transfer.getTransactionUUID()));
        } catch (ValidationException ve) {
            sendErrorResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE, ve.getMessage());
        } catch (NotEnoughMoneyException | AccountNotFoundException e) {
            sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("There was internal error while processing request", e);
            //Hiding internal error.
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }

    }

    private MoneyTransferApiRequest tryConvertRequestBody(HttpServletRequest request) {
        try {
            return objectMapper.readValue(
                    request.getReader().lines().collect(Collectors.joining()),
                    MoneyTransferApiRequest.class
            );
        } catch (IOException e) {
            throw new BadRequestException("Bad request");
        }
    }

    private void sendOkResponse(HttpServletResponse response, TransferStatusApiResponse apiResponse) throws IOException {
        response.setContentType(supportedContentType);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(objectMapper.writeValueAsString(apiResponse));
    }

    private void sendErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType(supportedContentType);
        response.setStatus(code);
        response.getWriter().print(objectMapper.writeValueAsString(new ErrorResponse(code, message)));
    }

}
