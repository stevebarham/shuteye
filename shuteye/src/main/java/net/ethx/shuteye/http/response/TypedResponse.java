package net.ethx.shuteye.http.response;

public class TypedResponse<T> extends Response {
    private final T value;

    public TypedResponse(final Response response, final T value) {
        super(response.statusCode(), response.statusText(), response.headers());
        this.value = value;
    }

    public T value() {
        return value;
    }
}
