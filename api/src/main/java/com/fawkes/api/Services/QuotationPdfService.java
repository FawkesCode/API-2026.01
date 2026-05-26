package com.fawkes.api.Services;

import com.fawkes.api.Entities.Products;
import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Entities.PurchaseOrderItem;
import com.fawkes.api.Entities.Quotation;
import com.fawkes.api.Repositories.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotationPdfService {

    private final QuotationRepository quotationRepository;

    private static final PDType1Font FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font FONT_REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font FONT_ITALIC = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

    private static final float MARGIN = 50;
    private static final float PAGE_W = PDRectangle.A4.getWidth();
    private static final float PAGE_H = PDRectangle.A4.getHeight();
    private static final float CONTENT_W = PAGE_W - 2 * MARGIN;
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Resource generatePdf(Long quotationId) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));

        PurchaseOrder order = quotation.getPurchaseOrder();
        List<PurchaseOrderItem> items = order.getItems();

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float y;
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                y = PAGE_H - MARGIN;
                y = header(cs, quotation, y);
                y = orderInfo(cs, order, y);
                y = itemsTable(cs, items, y);
                footer(cs, quotation);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return new ByteArrayResource(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF da cotacao", e);
        }
    }

    private float header(PDPageContentStream cs, Quotation q, float y) throws Exception {
        cs.setFont(FONT_BOLD, 22);
        text(cs, "NEWE - We Logic", MARGIN, y);
        y -= 10;

        cs.setFont(FONT_REGULAR, 8);
        text(cs, "Sistema de Gestao de Compras", MARGIN, y);
        y -= 22;

        cs.setFont(FONT_BOLD, 16);
        text(cs, "SOLICITACAO DE COTACAO", MARGIN, y);
        y -= 8;

        cs.setFont(FONT_REGULAR, 8);
        text(cs, "Nr. " + q.getQuotationNumber(), MARGIN, y);

        String data = "Data: " + q.getCreatedAt().format(DTF);
        float dataW = FONT_REGULAR.getStringWidth(data) / 1000f * 8;
        text(cs, data, PAGE_W - MARGIN - dataW, y);
        y -= 18;

        line(cs, MARGIN, y, CONTENT_W);
        y -= 14;

        return y;
    }

    private float orderInfo(PDPageContentStream cs, PurchaseOrder o, float y) throws Exception {
        cs.setFont(FONT_BOLD, 10);
        text(cs, "Dados do Pedido", MARGIN, y);
        y -= 16;

        cs.setFont(FONT_REGULAR, 9);
        text(cs, "Pedido:", MARGIN + 10, y);
        text(cs, "PED-" + o.getId(), MARGIN + 100, y);
        y -= 14;

        text(cs, "Solicitante:", MARGIN + 10, y);
        text(cs, o.getCreatedBy().getUserName(), MARGIN + 100, y);
        y -= 14;

        String dept = o.getCreatedBy().getDepartments() != null
                ? o.getCreatedBy().getDepartments().getDepartamentName() : "-";
        text(cs, "Setor:", MARGIN + 10, y);
        text(cs, dept, MARGIN + 100, y);
        y -= 14;

        text(cs, "Data do Pedido:", MARGIN + 10, y);
        text(cs, o.getOrderDate().format(DTF), MARGIN + 100, y);
        y -= 14;

        y -= 6;
        line(cs, MARGIN, y, CONTENT_W);
        y -= 16;

        return y;
    }

    private float itemsTable(PDPageContentStream cs, List<PurchaseOrderItem> items, float y) throws Exception {
        cs.setFont(FONT_BOLD, 10);
        text(cs, "Itens para Cotacao", MARGIN, y);
        y -= 18;

        float[] cw = {30, 250, 70, 90, 100};
        float[] cx = {
            MARGIN,
            MARGIN + cw[0],
            MARGIN + cw[0] + cw[1],
            MARGIN + cw[0] + cw[1] + cw[2],
            MARGIN + cw[0] + cw[1] + cw[2] + cw[3]
        };
        float tableW = cw[0] + cw[1] + cw[2] + cw[3] + cw[4];

        cs.setFont(FONT_BOLD, 8);
        String[] headers = {"#", "Produto", "Quant.", "Unidade", "Preco Unit. (R$)"};
        for (int i = 0; i < headers.length; i++) {
            text(cs, headers[i], cx[i] + 4, y - 2);
        }
        line(cs, MARGIN, y - 14, tableW);
        y -= 16;

        cs.setFont(FONT_REGULAR, 8);
        int idx = 1;
        for (PurchaseOrderItem item : items) {
            Products p = item.getProduct();
            String unit = p.getMeasurementUnit() != null
                    ? switch (p.getMeasurementUnit().name()) {
                        case "METROS" -> "Metros";
                        case "CAIXAS" -> "Caixas";
                        case "LITROS" -> "Litros";
                        case "KILOGRAMAS" -> "Kg";
                        default -> p.getMeasurementUnit().name();
                    } : "-";

            text(cs, String.valueOf(idx++), cx[0] + 4, y - 2);
            text(cs, p.getProductName(), cx[1] + 4, y - 2);
            text(cs, String.valueOf(item.getQuantity()), cx[2] + 4, y - 2);
            text(cs, unit, cx[3] + 4, y - 2);
            text(cs, "________________", cx[4] + 4, y - 2);

            line(cs, MARGIN, y - 3, tableW);
            y -= 15;
        }

        cs.setFont(FONT_ITALIC, 8);
        text(cs, "TOTAL R$: ____________________", cx[3] + 4, y - 6);
        y -= 22;

        line(cs, MARGIN, y, CONTENT_W);
        y -= 16;

        cs.setFont(FONT_BOLD, 10);
        text(cs, "Preencher pelo Fornecedor", MARGIN, y);
        y -= 16;

        cs.setFont(FONT_REGULAR, 9);
        String[] fields = {
            "Prazo de entrega: _________________________________ dias",
            "Condicoes de pagamento: _______________________________",
            "Observacoes: __________________________________________",
            "",
            "Fornecedor: ___________________________________________",
            "CNPJ: ________________________  Data: ___/___/______",
        };
        for (String f : fields) {
            if (f.isEmpty()) { y -= 4; continue; }
            text(cs, f, MARGIN + 10, y);
            y -= 16;
        }

        return y;
    }

    private void footer(PDPageContentStream cs, Quotation q) throws Exception {
        float y = 60;
        line(cs, MARGIN, y + 8, CONTENT_W);
        cs.setFont(FONT_REGULAR, 7);
        text(cs, "NEWE - We Logic  |  " + q.getQuotationNumber() + "  |  Sistema de Gestao de Compras", MARGIN, y);
    }

    private void text(PDPageContentStream cs, String t, float x, float y) throws Exception {
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(t);
        cs.endText();
    }

    private void line(PDPageContentStream cs, float x, float y, float w) throws Exception {
        cs.setLineWidth(0.5f);
        cs.moveTo(x, y);
        cs.lineTo(x + w, y);
        cs.stroke();
    }
}
