package com.gls.athena.starter.web.support;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;

import java.io.IOException;

/**
 * RequestBodyFilter 用于解决流只能读取一次的问题
 *
 * @author george
 */
@Component
public class RequestBodyFilter extends OncePerRequestFilter implements OrderedFilter {

    /**
     * 执行过滤器逻辑，对请求和响应进行处理。
     * 该方法重写了父类的doFilterInternal方法，用于在过滤器链中执行自定义的请求处理逻辑。
     * 主要功能是将请求体进行包装，并将包装后的请求传递给过滤器链中的下一个过滤器。
     *
     * @param request     HTTP请求对象，包含客户端发送的请求信息。
     * @param response    HTTP响应对象，用于向客户端发送响应信息。
     * @param filterChain 过滤器链对象，用于调用链中的下一个过滤器或目标资源。
     * @throws ServletException 如果处理请求时发生Servlet相关的异常。
     * @throws IOException      如果处理请求或响应时发生I/O相关的异常。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 跳过文件上传请求的包装
        if (MultipartResolutionDelegate.isMultipartRequest(request)) {
            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(new RequestBodyWrapper(request), response);
        }
    }

    /**
     * 获取当前过滤器的执行顺序。
     * <p>
     * 该方法返回一个整数值，表示当前过滤器在过滤器链中的执行顺序。
     * 通过从 `REQUEST_WRAPPER_FILTER_MAX_ORDER` 中减去 10000，确保该过滤器在过滤器链中较早执行。
     *
     * @return int 返回当前过滤器的执行顺序值。
     */
    @Override
    public int getOrder() {
        return REQUEST_WRAPPER_FILTER_MAX_ORDER - 10000;
    }
}
