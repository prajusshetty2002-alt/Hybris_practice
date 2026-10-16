package com.mycompany.mycustomwebservices.controllers;

import com.mycompany.mycustomextension.service.ErpOrderProcessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/myerp")
public class ErpWebhookController
{
    @Autowired
    private ErpOrderProcessService erpOrderProcessService;

    @PostMapping("/callback")
    public ResponseEntity<String> receiveErpResponse(
            @RequestParam String processCode,
            @RequestParam String statusCode)
    {
        System.out.println("========== ERP WEBHOOK HIT ==========");

        boolean processed =
                erpOrderProcessService.processErpResponse(
                        processCode,
                        statusCode);

        if (processed)
        {
            return ResponseEntity.ok(
                    "ERP response received");
        }

        return ResponseEntity.internalServerError()
                .body("Unable to process ERP response");
    }
}