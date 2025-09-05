package com.dap.sequence.server.controller.v1;

import com.dap.sequence.api.commons.Result;
import com.dap.sequence.api.commons.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/seq/detect")
public class SeqServerDetectController {


    @GetMapping("/do-detect")
    public Result seqDetect() {
        return ResultUtils.success("hello ,welcome to seq server !!!");
    }

}
