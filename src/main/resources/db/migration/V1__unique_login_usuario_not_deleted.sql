CREATE UNIQUE INDEX uk_usuario_email_not_deleted
    ON usuario (login)
    WHERE deletado = false;
