package com.shevaalex.android.rickmortydatabase.source.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import retrofit2.Response;

public class ApiResponse<T> {

    public ApiResponse<T> create(Throwable error) {
        String errorMessage = "Unknown error";
        if (error.getMessage() != null
        && !error.getMessage().equals("")) {
            errorMessage = error.getMessage();
        }
        return new ErrorApiResponse<>(errorMessage);
    }

    public ApiResponse<T> create(Response<T> response) {
        if (response.isSuccessful()) {
            T body = response.body();
            //response body is empty or has 204 empty response code
            if (body == null || response.code() == 204) {
                return new EmptyApiResponse<>();
            } else {
                return new SuccessApiResponse<>(body);
            }
        } else {
            String errorMessage = null;
            if (response.errorBody() != null) {
                try {
                    errorMessage = response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new ErrorApiResponse<>(errorMessage);
        }
    }

    public static class SuccessApiResponse<T> extends ApiResponse<T> {
        private T body;

        SuccessApiResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }

        @NonNull
        @Override
        public String toString() {
            return "SuccessApiResponse{" +
                    "body=" + body +
                    '}';
        }
    }

    public static class ErrorApiResponse<T> extends ApiResponse<T> {
        private String errorMessage;

        ErrorApiResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        @NonNull
        @Override
        public String toString() {
            return "ErrorApiResponse{" +
                    "errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }

    public static class EmptyApiResponse<T> extends ApiResponse<T> {}

}
