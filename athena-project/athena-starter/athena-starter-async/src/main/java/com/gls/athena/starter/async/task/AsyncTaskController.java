package com.gls.athena.starter.async.task;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/async")
@Tag(name = "async", description = "异步任务管理")
public class AsyncTaskController {
}
