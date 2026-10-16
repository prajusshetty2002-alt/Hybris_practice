//package com.mycompany.mycustomextension.controller;
//
//import com.mycompany.mycustomextension.service.ErpOrderProcessService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/erp")
//public class ErpWebhookController
//{
//    @Autowired
//    private ErpOrderProcessService erpOrderProcessService;
//
//
//    @PostMapping("/webhook")
//    public ResponseEntity<String> receiveErpResponse(
//            @RequestParam String processCode,
//            @RequestParam String statusCode)
//    {
//        boolean processed =
//                erpOrderProcessService.processErpResponse(
//                        processCode,
//                        statusCode);
//
//
//        if (processed)
//        {
//            return ResponseEntity.ok(
//                    "ERP response received");
//        }
//
//
//        return ResponseEntity.internalServerError()
//                .body("Unable to process ERP response");
//    }
//}