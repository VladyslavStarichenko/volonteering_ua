package nure.ua.volunteering_ua.service.statistics;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import nure.ua.volunteering_ua.dto.organization.OrganizationGetDto;
import nure.ua.volunteering_ua.dto.payment.BalanceDto;
import nure.ua.volunteering_ua.dto.payment.MoneyDto;
import nure.ua.volunteering_ua.dto.payment.TransactionDto;
import nure.ua.volunteering_ua.exeption.CustomException;
import nure.ua.volunteering_ua.model.Statistic;
import nure.ua.volunteering_ua.service.organization.OrganizationService;
import nure.ua.volunteering_ua.service.payment.StripeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Service
public class StatisticService {

    private final OrganizationService organizationService;
    private final StripeClient stripeClient;


    @Autowired
    public StatisticService(OrganizationService organizationService, StripeClient stripeClient) {
        this.organizationService = organizationService;
        this.stripeClient = stripeClient;
    }

    public byte[] generatePdf(String organizationName, int limitForTransactions) {

        OrganizationGetDto organization = organizationService.getOrganizationByName(organizationName);
        BalanceDto balance = stripeClient.getBalance(organizationName);
        List<TransactionDto> transactions = stripeClient.getTransactions(organizationName, limitForTransactions);
        if(!transactions.isEmpty()){
            if(limitForTransactions > transactions.size()){
                limitForTransactions = transactions.size();
            }
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                Document document = new Document();
                PdfWriter.getInstance(document, outputStream);
                document.open();
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
                Paragraph titleParagraph = new Paragraph(organization.getName(), titleFont);
                titleParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(titleParagraph);
                Image logoImage = Image.getInstance(organization.getImageURL());
                logoImage.setAlignment(Element.ALIGN_RIGHT);
                logoImage.scaleAbsolute(80, 80);
                document.add(logoImage);
                addSection(document, "Organization Statistics", formatStatistics(organization.getStatistic()));
                addSection(document, "Organization Rating", String.valueOf(organization.getRating()));
                addSection(document, "Balance", formatBalance(balance));
                addSection(document, "Transactions", formatTransactions(transactions, limitForTransactions));
                document.close();
                return outputStream.toByteArray();
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                throw new CustomException("There was an error during statistic document creation", HttpStatus.BAD_REQUEST);
            }
        }
        else {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                Document document = new Document();
                PdfWriter.getInstance(document, outputStream);
                document.open();
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
                Paragraph titleParagraph = new Paragraph(organization.getName(), titleFont);
                titleParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(titleParagraph);
                Image logoImage = Image.getInstance(organization.getImageURL());
                logoImage.setAlignment(Element.ALIGN_RIGHT);
                logoImage.scaleAbsolute(80, 80);
                document.add(logoImage);
                addSection(document, "Organization Statistics", formatStatistics(organization.getStatistic()));
                addSection(document, "Organization Rating", String.valueOf(organization.getRating()));
                document.close();
                return outputStream.toByteArray();
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                throw new CustomException("There was an error during statistic document creation", HttpStatus.BAD_REQUEST);
            }
        }

    }

    private void addSection(Document document, String sectionTitle, String sectionData) throws DocumentException {
        // Create section title
        Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.BOLD);
        Paragraph sectionTitleParagraph = new Paragraph(sectionTitle, sectionTitleFont);
        document.add(sectionTitleParagraph);

        // Create section data
        Font sectionDataFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
        Paragraph sectionDataParagraph = new Paragraph(sectionData, sectionDataFont);
        document.add(sectionDataParagraph);

        // Add spacing after the section
        document.add(Chunk.NEWLINE);
    }

    private String formatStatistics(Statistic statistic) {

        return "Total Requests: " + statistic.getRequestsCount() + "\n" +
                "Delivered Requests: " + statistic.getDeliveredCount() + "\n" +
                "Approved Requests: " + statistic.getApprovedRequestsCount() + "\n" +
                "In Verification Requests: " + statistic.getInVerificationRequestsCount() + "\n";
    }

    private String formatBalance(BalanceDto balance) {
        StringBuilder sb = new StringBuilder();

        sb.append("Available Balance:\n");
        for (MoneyDto money : balance.getAvailable()) {
            sb.append(money.getAmount()).append(" ").append(money.getCurrency()).append("\n");
        }

        sb.append("\nPending Balance:\n");
        for (MoneyDto money : balance.getPending()) {
            sb.append(money.getAmount()).append(" ").append(money.getCurrency()).append("\n");
        }

        return sb.toString();
    }

    private String formatTransactions(List<TransactionDto> transactions, int limit) {

        StringBuilder sb = new StringBuilder("Amount of transactions: " + String.valueOf(limit) + "\n");

        for (int i = 0; i < transactions.size(); i++) {
            TransactionDto transaction = transactions.get(i);
            checkTransaction(transaction);
            sb.append("\nTransaction ID: ").append(transaction.getId()).append("\n");
            sb.append("Amount: ").append(transaction.getAmount()).append(" ")
                    .append(transaction.getCurrency()).append("\n");
            sb.append("Application: ").append(transaction.getApplication()).append("\n");
            sb.append("Customer Email: ").append(transaction.getCustomerEmail()).append("\n");
            sb.append("Description: ").append(transaction.getDescription());

            if (i < transactions.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    private void checkTransaction(TransactionDto transaction) {
        if (transaction.getApplication() == null) {
            transaction.setApplication("Volunteerin_UA");
        }
        if (transaction.getCustomerEmail() == null) {
            transaction.setCustomerEmail("hidden");
        }
        if (transaction.getDescription() == null) {
            transaction.setDescription("Donation");
        }
    }
}
