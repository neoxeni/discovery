package com.mercury.discovery.common.excel;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component("ExcelDownloadView")
public class ExcelDownloadView extends AbstractView {

    private ExcelDownloadView() {
        this.setContentType("application/vnd.ms-excel");
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {

        ResultExcelDataHandler resultExcelDataHandler =
                (ResultExcelDataHandler) request.getAttribute(ResultExcelDataHandler.KEY_NAME);

        resultExcelDataHandler.download();

    }
}
