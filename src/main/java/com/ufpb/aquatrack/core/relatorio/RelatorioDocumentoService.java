package com.ufpb.aquatrack.core.relatorio;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;

@Service
public class RelatorioDocumentoService {

    private final SpringTemplateEngine templateEngine;

    public RelatorioDocumentoService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String gerarHtml(RelatorioCiclo relatorio) {
        Context context = new Context();
        context.setVariable("relatorio", relatorio);

        return templateEngine.process("relatorio/preview", context);
    }

    public byte[] gerarPdf(RelatorioCiclo relatorio) {
        try {
            String html = gerarHtml(relatorio);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();

            return out.toByteArray();

        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar PDF do relatório", e);
        }
    }
}
