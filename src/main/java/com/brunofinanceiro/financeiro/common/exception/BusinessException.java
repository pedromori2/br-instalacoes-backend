package com.brunofinanceiro.financeiro.common.exception;

/**
 * Violacao de uma regra de negocio (ex: editar lancamento de um caixa ja fechado).
 * Mapeada para HTTP 409 Conflict pelo GlobalExceptionHandler.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
