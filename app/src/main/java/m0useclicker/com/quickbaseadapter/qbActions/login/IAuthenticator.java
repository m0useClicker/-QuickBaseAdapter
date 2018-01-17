package m0useclicker.com.quickbaseadapter.qbActions.login;

public interface IAuthenticator {
    String getTicket(final String realmName, final String userId, final String password);
}
