package org.debtcrusher.ddd.controller.advice;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.debtcrusher.ddd.domain.exception.DebtAccessDeniedException;
import org.debtcrusher.ddd.domain.exception.DebtNotFoundException;
import org.debtcrusher.ddd.domain.exception.DomainException;
import org.debtcrusher.ddd.domain.exception.InvalidCredentialsException;
import org.debtcrusher.ddd.domain.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Dùng RFC7807 ProblemDetail (chuẩn Spring) thay vì tự bọc {code, message, data} — response
 * thành công trả thẳng DTO, không bọc thêm gì.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(DebtNotFoundException.class)
    public ProblemDetail handleDebtNotFound(DebtNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DebtAccessDeniedException.class)
    public ProblemDetail handleDebtAccessDenied(DebtAccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Fallback cho DomainException chưa có handler riêng (vd InvalidDebtRequestException,
     * PayoffNeverCompletesException, NoActiveDebtsException...) — luôn map về 400, hợp lý vì
     * đây đều là lỗi input/trạng thái phía client.
     */
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ProblemDetail handleRateLimited(RequestNotPermitted ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, "Quá nhiều yêu cầu, vui lòng thử lại sau");
    }
}
