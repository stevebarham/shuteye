package net.ethx.shuteye.http;

import net.ethx.shuteye.util.Preconditions;

public class ContentType {
    public static final ContentType WILDCARD = ContentType.parse("*");
    public static final ContentType APPLICATION_OCTET_STREAM = ContentType.create("application", "octet-stream");
    public static final ContentType MULTIPART_FORM_DATA = ContentType.create("multipart", "form-data");
    public static final ContentType TEXT_PLAIN = ContentType.create("text", "plain");

    private final String type;
    private final String subtype;

    ContentType(final String type, final String subtype) {
        Preconditions.checkArgument(!type.isEmpty() && !subtype.isEmpty(), "Invalid arguments type:'%s', subtype:'%s'", type, subtype);
        Preconditions.checkArgument(!"*".equals(type) || "*".equals(subtype), "Wildcard type provided, but non-wildcard subtype '%s' provided", subtype);

        this.type = type;
        this.subtype = subtype;
    }

    @Override
    public String toString() {
        return type + "/" + subtype;
    }

    public static ContentType create(final String type, final String subtype) {
        return new ContentType(type, subtype);
    }

    public static ContentType parse(final String contentType) {
        final int index = contentType.indexOf('/');
        final String type = index < 0 ? contentType : contentType.substring(0, index);
        final String subtype = index < 0 ? "*" : contentType.substring(index + 1);
        return new ContentType(type, subtype);
    }
}
