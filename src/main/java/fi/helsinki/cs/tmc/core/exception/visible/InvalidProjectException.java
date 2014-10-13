package fi.helsinki.cs.tmc.core.exception.visible;

import fi.helsinki.cs.tmc.core.exception.UserVisibleException;

public class InvalidProjectException extends UserVisibleException {

    public InvalidProjectException() {

        super();
    }

    public InvalidProjectException(final String message, final Throwable cause) {

        super(message, cause);
    }

    public InvalidProjectException(final String message) {

        super(message);
    }

    public InvalidProjectException(final Throwable cause) {

        super(cause);
    }

}
