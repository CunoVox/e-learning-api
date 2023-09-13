package com.elearning.email;

import com.elearning.controller.UserController;
import com.elearning.entities.VerificationCode;
import com.elearning.models.dtos.UserDTO;
import com.elearning.utils.enumAttribute.EnumVerificationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.elearning.utils.Constants.SERVICE_URL;

@Service
@RequiredArgsConstructor
public class EmailController implements EmailSender {

    private final JavaMailSender mailSender;
    private final UserController userController;
    private final String emailConfirmLink = SERVICE_URL + "/api/user/email/verify/";
    private final String SUBJECT_RESET_PASSWORD_EMAIL = "[E-Learning WISDOM] Reset Password của bạn";
    private final String SUBJECT_EMAIL_VERIFY = "[E-Learning WISDOM] Xác nhận Email của bạn";


    @Override
    @Async
    public void send(String to, String subject, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("thomsonbel12@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    @Override
    @Async
    public void sendMail(String to, VerificationCode code) {
        if (code.getType().equals(EnumVerificationCode.EMAIL_CONFIRM)) {
            sendUserEmailVerification(to, code);
        } else if (code.getType().equals(EnumVerificationCode.RESET_PASSWORD_CONFIRM)) {
            sendResetPasswordEmail(to, code);
        } else if (code.getType().equals(EnumVerificationCode.CHANGE_PASSWORD_CONFIRM)) {
            return;
        } else if (code.getType().equals(EnumVerificationCode.CHANGE_EMAIL_CONFIRM)) {
            return;
        }
    }
    @Async
    protected void sendUserEmailVerification(String to, VerificationCode code) {
        UserDTO dto = userController.findByEmail(to);
        String buildEmail = buildUserEmailVerification(to, emailConfirmLink + dto.getId() + "/"+ code.getCode());
        send(to, SUBJECT_EMAIL_VERIFY, buildEmail);
    }
    @Async
    protected void sendResetPasswordEmail(String to, VerificationCode code) {
        String buildEmail = buildResetPasswordEmail(to, code.getCode());
        send(to, SUBJECT_RESET_PASSWORD_EMAIL, buildEmail);
    }

    private String buildResetPasswordEmail(String to, String code) {
        return "<!DOCTYPE html\n" +
                "  PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
                "\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n" +
                "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "  <meta content=\"telephone=no\" name=\"format-detection\">\n" +
                "  <title>New message</title>\n" +
                "  <style type=\"text/css\">\n" +
                "    .rollover:hover .rollover-first {\n" +
                "      max-height: 0px !important;\n" +
                "      display: none !important;\n" +
                "    }\n" +
                "\n" +
                "    .rollover:hover .rollover-second {\n" +
                "      max-height: none !important;\n" +
                "      display: inline-block !important;\n" +
                "    }\n" +
                "\n" +
                "    .rollover div {\n" +
                "      font-size: 0px;\n" +
                "    }\n" +
                "\n" +
                "    u~div img+div>div {\n" +
                "      display: none;\n" +
                "    }\n" +
                "\n" +
                "    #outlook a {\n" +
                "      padding: 0;\n" +
                "    }\n" +
                "\n" +
                "    span.MsoHyperlink,\n" +
                "    span.MsoHyperlinkFollowed {\n" +
                "      color: inherit;\n" +
                "      mso-style-priority: 99;\n" +
                "    }\n" +
                "\n" +
                "    a.es-button {\n" +
                "      mso-style-priority: 100 !important;\n" +
                "      text-decoration: none !important;\n" +
                "    }\n" +
                "\n" +
                "    a[x-apple-data-detectors] {\n" +
                "      color: inherit !important;\n" +
                "      text-decoration: none !important;\n" +
                "      font-size: inherit !important;\n" +
                "      font-family: inherit !important;\n" +
                "      font-weight: inherit !important;\n" +
                "      line-height: inherit !important;\n" +
                "    }\n" +
                "\n" +
                "    .es-desk-hidden {\n" +
                "      display: none;\n" +
                "      float: left;\n" +
                "      overflow: hidden;\n" +
                "      width: 0;\n" +
                "      max-height: 0;\n" +
                "      line-height: 0;\n" +
                "      mso-hide: all;\n" +
                "    }\n" +
                "\n" +
                "    .es-header-body a:hover {\n" +
                "      color: #2d3142 !important;\n" +
                "    }\n" +
                "\n" +
                "    .es-content-body a:hover {\n" +
                "      color: #2d3142 !important;\n" +
                "    }\n" +
                "\n" +
                "    .es-footer-body a:hover {\n" +
                "      color: #2d3142 !important;\n" +
                "    }\n" +
                "\n" +
                "    .es-infoblock a:hover {\n" +
                "      color: #cccccc !important;\n" +
                "    }\n" +
                "\n" +
                "    .es-button-border:hover>a.es-button {\n" +
                "      color: #ffffff !important;\n" +
                "    }\n" +
                "\n" +
                "    @media only screen and (max-width:600px) {\n" +
                "      *[class=\"gmail-fix\"] {\n" +
                "        display: none !important\n" +
                "      }\n" +
                "\n" +
                "      p,\n" +
                "      a {\n" +
                "        line-height: 150% !important\n" +
                "      }\n" +
                "\n" +
                "      h1,\n" +
                "      h1 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h2,\n" +
                "      h2 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h3,\n" +
                "      h3 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h4,\n" +
                "      h4 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h5,\n" +
                "      h5 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h6,\n" +
                "      h6 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h1 {\n" +
                "        font-size: 30px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      h2 {\n" +
                "        font-size: 24px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      h3 {\n" +
                "        font-size: 20px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      h4 {\n" +
                "        font-size: 24px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      h5 {\n" +
                "        font-size: 20px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      h6 {\n" +
                "        font-size: 16px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h1 a,\n" +
                "      .es-content-body h1 a,\n" +
                "      .es-footer-body h1 a {\n" +
                "        font-size: 30px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h2 a,\n" +
                "      .es-content-body h2 a,\n" +
                "      .es-footer-body h2 a {\n" +
                "        font-size: 24px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h3 a,\n" +
                "      .es-content-body h3 a,\n" +
                "      .es-footer-body h3 a {\n" +
                "        font-size: 20px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h4 a,\n" +
                "      .es-content-body h4 a,\n" +
                "      .es-footer-body h4 a {\n" +
                "        font-size: 24px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h5 a,\n" +
                "      .es-content-body h5 a,\n" +
                "      .es-footer-body h5 a {\n" +
                "        font-size: 20px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h6 a,\n" +
                "      .es-content-body h6 a,\n" +
                "      .es-footer-body h6 a {\n" +
                "        font-size: 16px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-menu td a {\n" +
                "        font-size: 14px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body p,\n" +
                "      .es-header-body a {\n" +
                "        font-size: 14px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-content-body p,\n" +
                "      .es-content-body a {\n" +
                "        font-size: 14px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-footer-body p,\n" +
                "      .es-footer-body a {\n" +
                "        font-size: 14px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-infoblock p,\n" +
                "      .es-infoblock a {\n" +
                "        font-size: 12px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-c,\n" +
                "      .es-m-txt-c h1,\n" +
                "      .es-m-txt-c h2,\n" +
                "      .es-m-txt-c h3,\n" +
                "      .es-m-txt-c h4,\n" +
                "      .es-m-txt-c h5,\n" +
                "      .es-m-txt-c h6 {\n" +
                "        text-align: center !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-r,\n" +
                "      .es-m-txt-r h1,\n" +
                "      .es-m-txt-r h2,\n" +
                "      .es-m-txt-r h3,\n" +
                "      .es-m-txt-r h4,\n" +
                "      .es-m-txt-r h5,\n" +
                "      .es-m-txt-r h6 {\n" +
                "        text-align: right !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-j,\n" +
                "      .es-m-txt-j h1,\n" +
                "      .es-m-txt-j h2,\n" +
                "      .es-m-txt-j h3,\n" +
                "      .es-m-txt-j h4,\n" +
                "      .es-m-txt-j h5,\n" +
                "      .es-m-txt-j h6 {\n" +
                "        text-align: justify !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-l,\n" +
                "      .es-m-txt-l h1,\n" +
                "      .es-m-txt-l h2,\n" +
                "      .es-m-txt-l h3,\n" +
                "      .es-m-txt-l h4,\n" +
                "      .es-m-txt-l h5,\n" +
                "      .es-m-txt-l h6 {\n" +
                "        text-align: left !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-r img,\n" +
                "      .es-m-txt-c img,\n" +
                "      .es-m-txt-l img {\n" +
                "        display: inline !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-r .rollover:hover .rollover-second,\n" +
                "      .es-m-txt-c .rollover:hover .rollover-second,\n" +
                "      .es-m-txt-l .rollover:hover .rollover-second {\n" +
                "        display: inline !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-r .rollover div,\n" +
                "      .es-m-txt-c .rollover div,\n" +
                "      .es-m-txt-l .rollover div {\n" +
                "        line-height: 0 !important;\n" +
                "        font-size: 0 !important\n" +
                "      }\n" +
                "\n" +
                "      .es-spacer {\n" +
                "        display: inline-table\n" +
                "      }\n" +
                "\n" +
                "      a.es-button,\n" +
                "      button.es-button {\n" +
                "        font-size: 18px !important\n" +
                "      }\n" +
                "\n" +
                "      a.es-button,\n" +
                "      button.es-button {\n" +
                "        display: inline-block !important\n" +
                "      }\n" +
                "\n" +
                "      .es-button-border {\n" +
                "        display: inline-block !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-fw,\n" +
                "      .es-m-fw.es-fw,\n" +
                "      .es-m-fw .es-button {\n" +
                "        display: block !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-il,\n" +
                "      .es-m-il .es-button,\n" +
                "      .es-social,\n" +
                "      .es-social td,\n" +
                "      .es-menu {\n" +
                "        display: inline-block !important\n" +
                "      }\n" +
                "\n" +
                "      .es-adaptive table,\n" +
                "      .es-left,\n" +
                "      .es-right {\n" +
                "        width: 100% !important\n" +
                "      }\n" +
                "\n" +
                "      .es-content table,\n" +
                "      .es-header table,\n" +
                "      .es-footer table,\n" +
                "      .es-content,\n" +
                "      .es-footer,\n" +
                "      .es-header {\n" +
                "        width: 100% !important;\n" +
                "        max-width: 600px !important\n" +
                "      }\n" +
                "\n" +
                "      .adapt-img {\n" +
                "        width: 100% !important;\n" +
                "        height: auto !important\n" +
                "      }\n" +
                "\n" +
                "      .es-mobile-hidden,\n" +
                "      .es-hidden {\n" +
                "        display: none !important\n" +
                "      }\n" +
                "\n" +
                "      .es-desk-hidden {\n" +
                "        width: auto !important;\n" +
                "        overflow: visible !important;\n" +
                "        float: none !important;\n" +
                "        max-height: inherit !important;\n" +
                "        line-height: inherit !important\n" +
                "      }\n" +
                "\n" +
                "      tr.es-desk-hidden {\n" +
                "        display: table-row !important\n" +
                "      }\n" +
                "\n" +
                "      table.es-desk-hidden {\n" +
                "        display: table !important\n" +
                "      }\n" +
                "\n" +
                "      td.es-desk-menu-hidden {\n" +
                "        display: table-cell !important\n" +
                "      }\n" +
                "\n" +
                "      .es-menu td {\n" +
                "        width: 1% !important\n" +
                "      }\n" +
                "\n" +
                "      table.es-table-not-adapt,\n" +
                "      .esd-block-html table {\n" +
                "        width: auto !important\n" +
                "      }\n" +
                "\n" +
                "      .es-social td {\n" +
                "        padding-bottom: 10px\n" +
                "      }\n" +
                "\n" +
                "      .h-auto {\n" +
                "        height: auto !important\n" +
                "      }\n" +
                "\n" +
                "      a.es-button,\n" +
                "      button.es-button {\n" +
                "        border-top-width: 15px !important;\n" +
                "        border-bottom-width: 15px !important\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"width:100%;height:100%;padding:0;Margin:0\">\n" +
                "  <div class=\"es-wrapper-color\" style=\"background-color:#FFFFFF\"><!--[if gte mso 9]>\n" +
                "\t\t\t<v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\">\n" +
                "\t\t\t\t<v:fill type=\"tile\" color=\"#ffffff\"></v:fill>\n" +
                "\t\t\t</v:background>\n" +
                "\t\t<![endif]-->\n" +
                "    <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                "      style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#FFFFFF\">\n" +
                "      <tr>\n" +
                "        <td valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                "          <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\"\n" +
                "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important\">\n" +
                "            <tr>\n" +
                "              <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "                <table bgcolor=\"#efefef\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#EFEFEF;border-radius:20px 20px 0 0;width:600px\">\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\" style=\"padding:0;Margin:0;padding-top:40px;padding-right:40px;padding-left:40px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                        <tr>\n" +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:520px\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"left\" class=\"es-m-txt-c\" style=\"padding:0;Margin:0;font-size:0px\"><a\n" +
                "                                    target=\"_blank\" href=\"https://viewstripo.email\"\n" +
                "                                    style=\"mso-line-height-rule:exactly;text-decoration:underline;color:#2D3142;font-size:18px\"><img\n" +
                "                                      src=\"https://res.cloudinary.com/dqy4p8xug/image/upload/v1694095924/E-Learning/logoELearn_dzx8fp.png\"\n" +
                "                                      alt=\"Confirm email\"\n" +
                "                                      style=\"display:block;font-size:18px;border:0;outline:none;text-decoration:none;border-radius:55px\"\n" +
                "                                      width=\"140\" title=\"Confirm email\"></a></td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\" style=\"padding:0;Margin:0;padding-right:40px;padding-left:40px;padding-top:20px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                        <tr>\n" +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:520px\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#fafafa\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:separate;border-spacing:0px;background-color:#fafafa;border-radius:10px\"\n" +
                "                              role=\"presentation\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                "                                  <h3\n" +
                "                                    style=\"Margin:0;font-family:Imprima, Arial, sans-serif;mso-line-height-rule:exactly;letter-spacing:0;font-size:28px;font-style:normal;font-weight:bold;line-height:34px;color:#2D3142\">\n" +
                "                                    Xin chào, " + to + "</h3>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    <br></p>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    \u200BBạn nhận được tin nhắn này sau yêu cầu tạo mới mật khẩu ở trang Web E-Learning.</p>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    \u200B</p>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    Xin vui lòng điền mã xác nhận phía dưới đây vào trang web của chúng tôi để tiếp tục\n" +
                "                                    thủ tục tạo mới mật khẩu.</p>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    &nbsp;</p>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "          <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\"\n" +
                "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important\">\n" +
                "            <tr>\n" +
                "              <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "                <table bgcolor=\"#efefef\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#EFEFEF;width:600px\">\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\"\n" +
                "                      style=\"Margin:0;padding-right:40px;padding-left:40px;padding-top:30px;padding-bottom:40px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                        <tr>\n" +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:520px\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" style=\"padding:0;Margin:0\"><span class=\"es-button-border msohide\"\n" +
                "                                    style=\"border-style:solid;border-color:#2CB543;background:#7630f3;border-width:0px;display:block;border-radius:30px;width:auto;mso-hide:all\"><a\n" +
                "                                      href=\"\" class=\"es-button msohide\" target=\"_blank\"\n" +
                "                                      style=\"mso-style-priority:100 !important;text-decoration:none !important;mso-line-height-rule:exactly;color:#FFFFFF;font-size:22px;padding:15px 20px 15px 20px;display:block;background:#7630f3;border-radius:30px;font-family:Imprima, Arial, sans-serif;font-weight:bold;font-style:normal;line-height:26px !important;width:auto;text-align:center;letter-spacing:0;mso-padding-alt:0;mso-border-alt:10px solid  #7630f3;mso-hide:all;padding-left:5px;padding-right:5px\">" + code + "</a></span><!--<![endif]-->\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\" style=\"padding:0;Margin:0;padding-right:40px;padding-left:40px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                        <tr>\n" +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:520px\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    Xin cảm ơn,<br><br>E-Learning Team.</p>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\"\n" +
                "                                  style=\"padding:0;Margin:0;padding-top:40px;padding-bottom:20px;font-size:0\">\n" +
                "                                  <table border=\"0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                    role=\"presentation\"\n" +
                "                                    style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                                    <tr>\n" +
                "                                      <td\n" +
                "                                        style=\"padding:0;Margin:0;border-bottom:1px solid #666666;background:unset;height:1px;width:100%;margin:0px\">\n" +
                "                                      </td>\n" +
                "                                    </tr>\n" +
                "                                  </table>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "          <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\"\n" +
                "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important\">\n" +
                "            <tr>\n" +
                "              <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "                <table bgcolor=\"#efefef\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#EFEFEF;border-radius:0 0 20px 20px;width:600px\">\n" +
                "                  <tr>\n" +
                "                    <td class=\"esdev-adapt-off\" align=\"left\"\n" +
                "                      style=\"Margin:0;padding-right:40px;padding-left:40px;padding-top:20px;padding-bottom:20px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" class=\"esdev-mso-table\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:520px\">\n" +
                "                        <tr>\n" +
                "                          <td class=\"esdev-mso-td\" valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:47px\">\n" +
                "                                  <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                                    style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                                    <tr>\n" +
                "                                      <td align=\"center\" class=\"es-m-txt-l\" style=\"padding:0;Margin:0;font-size:0px\"><a\n" +
                "                                          target=\"_blank\" href=\"https://viewstripo.email\"\n" +
                "                                          style=\"mso-line-height-rule:exactly;text-decoration:underline;color:#2D3142;font-size:18px\"><img\n" +
                "                                            src=\"https://syqnyu.stripocdn.email/content/guids/CABINET_ee77850a5a9f3068d9355050e69c76d26d58c3ea2927fa145f0d7a894e624758/images/group_4076325.png\"\n" +
                "                                            alt=\"Demo\"\n" +
                "                                            style=\"display:block;font-size:18px;border:0;outline:none;text-decoration:none\"\n" +
                "                                            width=\"47\" title=\"Demo\"></a></td>\n" +
                "                                    </tr>\n" +
                "                                  </table>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                          <td style=\"padding:0;Margin:0;width:20px\"></td>\n" +
                "                          <td class=\"esdev-mso-td\" valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:453px\">\n" +
                "                                  <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                                    style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                                    <tr>\n" +
                "                                      <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
                "                                        <p\n" +
                "                                          style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:24px;letter-spacing:0;color:#2D3142;font-size:16px\">\n" +
                "                                          Đường dẫn này sẽ hết hạn trong vòng 5 phút, nếu có câu hỏi nói xin hãy liên hệ\n" +
                "                                          <a target=\"_blank\" href=\"https://viewstripo.em\"\n" +
                "                                            style=\"mso-line-height-rule:exactly;text-decoration:underline;color:#2D3142;font-size:16px !important;line-height:24px !important\">quản\n" +
                "                                            trị viên.</a></p>\n" +
                "                                      </td>\n" +
                "                                    </tr>\n" +
                "                                  </table>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "          <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-footer\" align=\"center\"\n" +
                "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                "            <tr>\n" +
                "              <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "                <table bgcolor=\"#bcb8b1\" class=\"es-footer-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px\">\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\"\n" +
                "                      style=\"Margin:0;padding-top:40px;padding-right:20px;padding-bottom:30px;padding-left:20px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                        <tr>\n" +
                "                          <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" class=\"es-m-txt-c\"\n" +
                "                                  style=\"padding:0;Margin:0;padding-bottom:20px;padding-top:10px;font-size:0\">\n" +
                "                                  <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-table-not-adapt es-social\"\n" +
                "                                    role=\"presentation\"\n" +
                "                                    style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                                    <tr>\n" +
                "                                      <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;padding-right:5px\"><img\n" +
                "                                          src=\"https://syqnyu.stripocdn.email/content/assets/img/social-icons/logo-black/x-logo-black.png\"\n" +
                "                                          alt=\"X\" title=\"X.com\" height=\"24\"\n" +
                "                                          style=\"display:block;font-size:18px;border:0;outline:none;text-decoration:none\">\n" +
                "                                      </td>\n" +
                "                                      <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;padding-right:5px\"><img\n" +
                "                                          src=\"https://syqnyu.stripocdn.email/content/assets/img/social-icons/logo-black/facebook-logo-black.png\"\n" +
                "                                          alt=\"Fb\" title=\"Facebook\" height=\"24\"\n" +
                "                                          style=\"display:block;font-size:18px;border:0;outline:none;text-decoration:none\">\n" +
                "                                      </td>\n" +
                "                                      <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0\"><img\n" +
                "                                          src=\"https://syqnyu.stripocdn.email/content/assets/img/social-icons/logo-black/linkedin-logo-black.png\"\n" +
                "                                          alt=\"In\" title=\"Linkedin\" height=\"24\"\n" +
                "                                          style=\"display:block;font-size:18px;border:0;outline:none;text-decoration:none\">\n" +
                "                                      </td>\n" +
                "                                    </tr>\n" +
                "                                  </table>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:20px;letter-spacing:0;color:#2D3142;font-size:13px\">\n" +
                "                                    <a target=\"_blank\"\n" +
                "                                      style=\"mso-line-height-rule:exactly;text-decoration:none;color:#2D3142;font-size:14px\"\n" +
                "                                      href=\"\"></a><a target=\"_blank\"\n" +
                "                                      style=\"mso-line-height-rule:exactly;text-decoration:none;color:#2D3142;font-size:14px\"\n" +
                "                                      href=\"\">Privacy Policy</a><a target=\"_blank\"\n" +
                "                                      style=\"mso-line-height-rule:exactly;text-decoration:none;color:#2D3142;font-size:13px\"\n" +
                "                                      href=\"\"></a> • <a target=\"_blank\"\n" +
                "                                      style=\"mso-line-height-rule:exactly;text-decoration:none;color:#2D3142;font-size:14px\"\n" +
                "                                      href=\"\">Unsubscribe</a></p>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" style=\"padding:0;Margin:0;padding-top:20px\">\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:21px;letter-spacing:0;color:#2D3142;font-size:14px\">\n" +
                "                                    Copyright © 2023 E-Learning Team</p>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
    }

    private String buildUserEmailVerification(String to, String link) {
        return "<!DOCTYPE html\n" +
                "  PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
                "\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta content=\"width=device-width, initial-scale=1\" name=\"viewport\">\n" +
                "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "  <meta content=\"telephone=no\" name=\"format-detection\">\n" +
                "  <title>New email template 2023-09-07</title><!--[if (mso 16)]>\n" +
                "  <link href=\"https://fonts.googleapis.com/css2?family=Imprima&display=swap\" rel=\"stylesheet\"><!--<![endif]-->\n" +
                "  <style type=\"text/css\">\n" +
                "    .rollover:hover .rollover-first {\n" +
                "      max-height: 0px !important;\n" +
                "      display: none !important;\n" +
                "    }\n" +
                "\n" +
                "    .rollover:hover .rollover-second {\n" +
                "      max-height: none !important;\n" +
                "      display: inline-block !important;\n" +
                "    }\n" +
                "\n" +
                "    .rollover div {\n" +
                "      font-size: 0px;\n" +
                "    }\n" +
                "\n" +
                "    u~div img+div>div {\n" +
                "      display: none;\n" +
                "    }\n" +
                "\n" +
                "    #outlook a {\n" +
                "      padding: 0;\n" +
                "    }\n" +
                "\n" +
                "    span.MsoHyperlink,\n" +
                "    span.MsoHyperlinkFollowed {\n" +
                "      color: inherit;\n" +
                "      mso-style-priority: 99;\n" +
                "    }\n" +
                "\n" +
                "    a.es-button {\n" +
                "      mso-style-priority: 100 !important;\n" +
                "      text-decoration: none !important;\n" +
                "    }\n" +
                "\n" +
                "    a[x-apple-data-detectors] {\n" +
                "      color: inherit !important;\n" +
                "      text-decoration: none !important;\n" +
                "      font-size: inherit !important;\n" +
                "      font-family: inherit !important;\n" +
                "      font-weight: inherit !important;\n" +
                "      line-height: inherit !important;\n" +
                "    }\n" +
                "\n" +
                "    .es-desk-hidden {\n" +
                "      display: none;\n" +
                "      float: left;\n" +
                "      overflow: hidden;\n" +
                "      width: 0;\n" +
                "      max-height: 0;\n" +
                "      line-height: 0;\n" +
                "      mso-hide: all;\n" +
                "    }\n" +
                "\n" +
                "    .es-header-body a:hover {\n" +
                "      color: #2d3142 !important;\n" +
                "    }\n" +
                "\n" +
                "    .es-content-body a:hover {\n" +
                "      color: #2d3142 !important;\n" +
                "    }\n" +
                "\n" +
                "    .es-footer-body a:hover {\n" +
                "      color: #2d3142 !important;\n" +
                "    }\n" +
                "\n" +
                "    .es-infoblock a:hover {\n" +
                "      color: #cccccc !important;\n" +
                "    }\n" +
                "\n" +
                "    .es-button-border:hover>a.es-button {\n" +
                "      color: #ffffff !important;\n" +
                "    }\n" +
                "\n" +
                "    @media only screen and (max-width:600px) {\n" +
                "      *[class=\"gmail-fix\"] {\n" +
                "        display: none !important\n" +
                "      }\n" +
                "\n" +
                "      p,\n" +
                "      a {\n" +
                "        line-height: 150% !important\n" +
                "      }\n" +
                "\n" +
                "      h1,\n" +
                "      h1 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h2,\n" +
                "      h2 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h3,\n" +
                "      h3 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h4,\n" +
                "      h4 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h5,\n" +
                "      h5 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h6,\n" +
                "      h6 a {\n" +
                "        line-height: 120% !important\n" +
                "      }\n" +
                "\n" +
                "      h1 {\n" +
                "        font-size: 30px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      h2 {\n" +
                "        font-size: 24px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      h3 {\n" +
                "        font-size: 20px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      h4 {\n" +
                "        font-size: 24px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      h5 {\n" +
                "        font-size: 20px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      h6 {\n" +
                "        font-size: 16px !important;\n" +
                "        text-align: left\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h1 a,\n" +
                "      .es-content-body h1 a,\n" +
                "      .es-footer-body h1 a {\n" +
                "        font-size: 30px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h2 a,\n" +
                "      .es-content-body h2 a,\n" +
                "      .es-footer-body h2 a {\n" +
                "        font-size: 24px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h3 a,\n" +
                "      .es-content-body h3 a,\n" +
                "      .es-footer-body h3 a {\n" +
                "        font-size: 20px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h4 a,\n" +
                "      .es-content-body h4 a,\n" +
                "      .es-footer-body h4 a {\n" +
                "        font-size: 24px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h5 a,\n" +
                "      .es-content-body h5 a,\n" +
                "      .es-footer-body h5 a {\n" +
                "        font-size: 20px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body h6 a,\n" +
                "      .es-content-body h6 a,\n" +
                "      .es-footer-body h6 a {\n" +
                "        font-size: 16px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-menu td a {\n" +
                "        font-size: 14px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-header-body p,\n" +
                "      .es-header-body a {\n" +
                "        font-size: 14px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-content-body p,\n" +
                "      .es-content-body a {\n" +
                "        font-size: 14px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-footer-body p,\n" +
                "      .es-footer-body a {\n" +
                "        font-size: 14px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-infoblock p,\n" +
                "      .es-infoblock a {\n" +
                "        font-size: 12px !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-c,\n" +
                "      .es-m-txt-c h1,\n" +
                "      .es-m-txt-c h2,\n" +
                "      .es-m-txt-c h3,\n" +
                "      .es-m-txt-c h4,\n" +
                "      .es-m-txt-c h5,\n" +
                "      .es-m-txt-c h6 {\n" +
                "        text-align: center !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-r,\n" +
                "      .es-m-txt-r h1,\n" +
                "      .es-m-txt-r h2,\n" +
                "      .es-m-txt-r h3,\n" +
                "      .es-m-txt-r h4,\n" +
                "      .es-m-txt-r h5,\n" +
                "      .es-m-txt-r h6 {\n" +
                "        text-align: right !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-j,\n" +
                "      .es-m-txt-j h1,\n" +
                "      .es-m-txt-j h2,\n" +
                "      .es-m-txt-j h3,\n" +
                "      .es-m-txt-j h4,\n" +
                "      .es-m-txt-j h5,\n" +
                "      .es-m-txt-j h6 {\n" +
                "        text-align: justify !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-l,\n" +
                "      .es-m-txt-l h1,\n" +
                "      .es-m-txt-l h2,\n" +
                "      .es-m-txt-l h3,\n" +
                "      .es-m-txt-l h4,\n" +
                "      .es-m-txt-l h5,\n" +
                "      .es-m-txt-l h6 {\n" +
                "        text-align: left !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-r img,\n" +
                "      .es-m-txt-c img,\n" +
                "      .es-m-txt-l img {\n" +
                "        display: inline !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-r .rollover:hover .rollover-second,\n" +
                "      .es-m-txt-c .rollover:hover .rollover-second,\n" +
                "      .es-m-txt-l .rollover:hover .rollover-second {\n" +
                "        display: inline !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-txt-r .rollover div,\n" +
                "      .es-m-txt-c .rollover div,\n" +
                "      .es-m-txt-l .rollover div {\n" +
                "        line-height: 0 !important;\n" +
                "        font-size: 0 !important\n" +
                "      }\n" +
                "\n" +
                "      .es-spacer {\n" +
                "        display: inline-table\n" +
                "      }\n" +
                "\n" +
                "      a.es-button,\n" +
                "      button.es-button {\n" +
                "        font-size: 18px !important\n" +
                "      }\n" +
                "\n" +
                "      a.es-button,\n" +
                "      button.es-button {\n" +
                "        display: block !important\n" +
                "      }\n" +
                "\n" +
                "      .es-button-border {\n" +
                "        display: block !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-fw,\n" +
                "      .es-m-fw.es-fw,\n" +
                "      .es-m-fw .es-button {\n" +
                "        display: block !important\n" +
                "      }\n" +
                "\n" +
                "      .es-m-il,\n" +
                "      .es-m-il .es-button,\n" +
                "      .es-social,\n" +
                "      .es-social td,\n" +
                "      .es-menu {\n" +
                "        display: inline-block !important\n" +
                "      }\n" +
                "\n" +
                "      .es-adaptive table,\n" +
                "      .es-left,\n" +
                "      .es-right {\n" +
                "        width: 100% !important\n" +
                "      }\n" +
                "\n" +
                "      .es-content table,\n" +
                "      .es-header table,\n" +
                "      .es-footer table,\n" +
                "      .es-content,\n" +
                "      .es-footer,\n" +
                "      .es-header {\n" +
                "        width: 100% !important;\n" +
                "        max-width: 600px !important\n" +
                "      }\n" +
                "\n" +
                "      .adapt-img {\n" +
                "        width: 100% !important;\n" +
                "        height: auto !important\n" +
                "      }\n" +
                "\n" +
                "      .es-mobile-hidden,\n" +
                "      .es-hidden {\n" +
                "        display: none !important\n" +
                "      }\n" +
                "\n" +
                "      .es-desk-hidden {\n" +
                "        width: auto !important;\n" +
                "        overflow: visible !important;\n" +
                "        float: none !important;\n" +
                "        max-height: inherit !important;\n" +
                "        line-height: inherit !important\n" +
                "      }\n" +
                "\n" +
                "      tr.es-desk-hidden {\n" +
                "        display: table-row !important\n" +
                "      }\n" +
                "\n" +
                "      table.es-desk-hidden {\n" +
                "        display: table !important\n" +
                "      }\n" +
                "\n" +
                "      td.es-desk-menu-hidden {\n" +
                "        display: table-cell !important\n" +
                "      }\n" +
                "\n" +
                "      .es-menu td {\n" +
                "        width: 1% !important\n" +
                "      }\n" +
                "\n" +
                "      table.es-table-not-adapt,\n" +
                "      .esd-block-html table {\n" +
                "        width: auto !important\n" +
                "      }\n" +
                "\n" +
                "      .es-social td {\n" +
                "        padding-bottom: 10px\n" +
                "      }\n" +
                "\n" +
                "      .h-auto {\n" +
                "        height: auto !important\n" +
                "      }\n" +
                "\n" +
                "      a.es-button,\n" +
                "      button.es-button {\n" +
                "        border-top-width: 15px !important;\n" +
                "        border-bottom-width: 15px !important\n" +
                "      }\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "\n" +
                "<body style=\"width:100%;height:100%;padding:0;Margin:0\">\n" +
                "  <div class=\"es-wrapper-color\" style=\"background-color:#FFFFFF\">\n" +
                "    <table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                "      style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top;background-color:#FFFFFF\">\n" +
                "      <tr>\n" +
                "        <td valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                "          <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\"\n" +
                "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important\">\n" +
                "            <tr>\n" +
                "              <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "                <table bgcolor=\"#efefef\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#EFEFEF;border-radius:20px 20px 0 0;width:600px\">\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\" style=\"padding:0;Margin:0;padding-top:40px;padding-right:40px;padding-left:40px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                        <tr>\n" +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:520px\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"left\" class=\"es-m-txt-c\" style=\"padding:0;Margin:0;font-size:0px\"><a\n" +
                "                                    target=\"_blank\" href=\"https://viewstripo.email\"\n" +
                "                                    style=\"mso-line-height-rule:exactly;text-decoration:underline;color:#2D3142;font-size:18px\"><img\n" +
                "                                      src=\"https://res.cloudinary.com/dqy4p8xug/image/upload/v1694095924/E-Learning/logoELearn_dzx8fp.png\"\n" +
                "                                      alt=\"Confirm email\"\n" +
                "                                      style=\"display:block;font-size:18px;border:0;outline:none;text-decoration:none;border-radius:55px\"\n" +
                "                                      width=\"140\" title=\"Confirm email\"></a></td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\" style=\"padding:0;Margin:0;padding-right:40px;padding-left:40px;padding-top:20px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                        <tr>\n" +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:520px\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" bgcolor=\"#fafafa\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:separate;border-spacing:0px;background-color:#fafafa;border-radius:10px\"\n" +
                "                              role=\"presentation\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"left\" style=\"padding:20px;Margin:0\">\n" +
                "                                  <h3\n" +
                "                                    style=\"Margin:0;font-family:Imprima, Arial, sans-serif;mso-line-height-rule:exactly;letter-spacing:0;font-size:28px;font-style:normal;font-weight:bold;line-height:34px;color:#2D3142\">\n" +
                "                                    Xin chào, " + to + "</h3>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    <br></p>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    &nbsp;</p>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    Bạn nhận được tin nhắn này sau khi đăng kí tài khoản ở trang Web E-Learning.</p>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    &nbsp;</p>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    Xin vui lòng xác nhận email của bạn bằng cách nhấn vào nút bên dưới. Đây là một bước\n" +
                "                                    để tăng cường bảo mật cho email của bạn khi sử dụng dịch vụ của chúng tôi.</p>\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    &nbsp;</p>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "          <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\"\n" +
                "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important\">\n" +
                "            <tr>\n" +
                "              <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "                <table bgcolor=\"#efefef\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#EFEFEF;width:600px\">\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\"\n" +
                "                      style=\"Margin:0;padding-right:40px;padding-left:40px;padding-top:30px;padding-bottom:40px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                        <tr>\n" +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:520px\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" style=\"padding:0;Margin:0\"><span class=\"es-button-border msohide\"\n" +
                "                                    style=\"border-style:solid;border-color:#2CB543;background:#7630f3;border-width:0px;display:block;border-radius:30px;width:auto;mso-hide:all\"><a\n" +
                "                                      href=\"" + link + "\" class=\"es-button msohide\" target=\"_blank\"\n" +
                "                                      style=\"mso-style-priority:100 !important;text-decoration:none !important;mso-line-height-rule:exactly;color:#FFFFFF;font-size:22px;padding:15px 20px 15px 20px;display:block;background:#7630f3;border-radius:30px;font-family:Imprima, Arial, sans-serif;font-weight:bold;font-style:normal;line-height:26px !important;width:auto;text-align:center;letter-spacing:0;mso-padding-alt:0;mso-border-alt:10px solid  #7630f3;mso-hide:all;padding-left:5px;padding-right:5px\">Xác\n" +
                "                                      nhận</a></span><!--<![endif]--></td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\" style=\"padding:0;Margin:0;padding-right:40px;padding-left:40px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                        <tr>\n" +
                "                          <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:520px\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:27px;letter-spacing:0;color:#2D3142;font-size:18px\">\n" +
                "                                    Xin cảm ơn,<br><br>E-Learning Team.</p>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\"\n" +
                "                                  style=\"padding:0;Margin:0;padding-top:40px;padding-bottom:20px;font-size:0\">\n" +
                "                                  <table border=\"0\" width=\"100%\" height=\"100%\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                    role=\"presentation\"\n" +
                "                                    style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                                    <tr>\n" +
                "                                      <td\n" +
                "                                        style=\"padding:0;Margin:0;border-bottom:1px solid #666666;background:unset;height:1px;width:100%;margin:0px\">\n" +
                "                                      </td>\n" +
                "                                    </tr>\n" +
                "                                  </table>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "          <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\"\n" +
                "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important\">\n" +
                "            <tr>\n" +
                "              <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "                <table bgcolor=\"#efefef\" class=\"es-content-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#EFEFEF;border-radius:0 0 20px 20px;width:600px\">\n" +
                "                  <tr>\n" +
                "                    <td class=\"esdev-adapt-off\" align=\"left\"\n" +
                "                      style=\"Margin:0;padding-right:40px;padding-left:40px;padding-top:20px;padding-bottom:20px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" class=\"esdev-mso-table\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:520px\">\n" +
                "                        <tr>\n" +
                "                          <td class=\"esdev-mso-td\" valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" align=\"left\" class=\"es-left\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:47px\">\n" +
                "                                  <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                                    style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                                    <tr>\n" +
                "                                      <td align=\"center\" class=\"es-m-txt-l\" style=\"padding:0;Margin:0;font-size:0px\"><a\n" +
                "                                          target=\"_blank\" href=\"https://viewstripo.email\"\n" +
                "                                          style=\"mso-line-height-rule:exactly;text-decoration:underline;color:#2D3142;font-size:18px\"><img\n" +
                "                                            src=\"https://syqnyu.stripocdn.email/content/guids/CABINET_ee77850a5a9f3068d9355050e69c76d26d58c3ea2927fa145f0d7a894e624758/images/group_4076325.png\"\n" +
                "                                            alt=\"Demo\"\n" +
                "                                            style=\"display:block;font-size:18px;border:0;outline:none;text-decoration:none\"\n" +
                "                                            width=\"47\" title=\"Demo\"></a></td>\n" +
                "                                    </tr>\n" +
                "                                  </table>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                          <td style=\"padding:0;Margin:0;width:20px\"></td>\n" +
                "                          <td class=\"esdev-mso-td\" valign=\"top\" style=\"padding:0;Margin:0\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-right\" align=\"right\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;width:453px\">\n" +
                "                                  <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                                    style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                                    <tr>\n" +
                "                                      <td align=\"left\" style=\"padding:0;Margin:0\">\n" +
                "                                        <p\n" +
                "                                          style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:24px;letter-spacing:0;color:#2D3142;font-size:16px\">\n" +
                "                                          Đường dẫn này sẽ hết hạn trong vòng 5 phút, nếu có câu hỏi nói xin hãy liên hệ\n" +
                "                                          <a target=\"_blank\" href=\"https://viewstripo.em\"\n" +
                "                                            style=\"mso-line-height-rule:exactly;text-decoration:underline;color:#2D3142;font-size:16px !important;line-height:24px !important\">quản\n" +
                "                                            trị viên.</a></p>\n" +
                "                                      </td>\n" +
                "                                    </tr>\n" +
                "                                  </table>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "          <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-footer\" align=\"center\"\n" +
                "            style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;width:100%;table-layout:fixed !important;background-color:transparent;background-repeat:repeat;background-position:center top\">\n" +
                "            <tr>\n" +
                "              <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "                <table bgcolor=\"#bcb8b1\" class=\"es-footer-body\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                  style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px\">\n" +
                "                  <tr>\n" +
                "                    <td align=\"left\"\n" +
                "                      style=\"Margin:0;padding-top:40px;padding-right:20px;padding-bottom:30px;padding-left:20px\">\n" +
                "                      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                        style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                        <tr>\n" +
                "                          <td align=\"left\" style=\"padding:0;Margin:0;width:560px\">\n" +
                "                            <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" role=\"presentation\"\n" +
                "                              style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" class=\"es-m-txt-c\"\n" +
                "                                  style=\"padding:0;Margin:0;padding-bottom:20px;padding-top:10px;font-size:0\">\n" +
                "                                  <table cellpadding=\"0\" cellspacing=\"0\" class=\"es-table-not-adapt es-social\"\n" +
                "                                    role=\"presentation\"\n" +
                "                                    style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\">\n" +
                "                                    <tr>\n" +
                "                                      <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;padding-right:5px\"><img\n" +
                "                                          src=\"https://syqnyu.stripocdn.email/content/assets/img/social-icons/logo-black/x-logo-black.png\"\n" +
                "                                          alt=\"X\" title=\"X.com\" height=\"24\"\n" +
                "                                          style=\"display:block;font-size:18px;border:0;outline:none;text-decoration:none\">\n" +
                "                                      </td>\n" +
                "                                      <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0;padding-right:5px\"><img\n" +
                "                                          src=\"https://syqnyu.stripocdn.email/content/assets/img/social-icons/logo-black/facebook-logo-black.png\"\n" +
                "                                          alt=\"Fb\" title=\"Facebook\" height=\"24\"\n" +
                "                                          style=\"display:block;font-size:18px;border:0;outline:none;text-decoration:none\">\n" +
                "                                      </td>\n" +
                "                                      <td align=\"center\" valign=\"top\" style=\"padding:0;Margin:0\"><img\n" +
                "                                          src=\"https://syqnyu.stripocdn.email/content/assets/img/social-icons/logo-black/linkedin-logo-black.png\"\n" +
                "                                          alt=\"In\" title=\"Linkedin\" height=\"24\"\n" +
                "                                          style=\"display:block;font-size:18px;border:0;outline:none;text-decoration:none\">\n" +
                "                                      </td>\n" +
                "                                    </tr>\n" +
                "                                  </table>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" style=\"padding:0;Margin:0\">\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:20px;letter-spacing:0;color:#2D3142;font-size:13px\">\n" +
                "                                    <a target=\"_blank\"\n" +
                "                                      style=\"mso-line-height-rule:exactly;text-decoration:none;color:#2D3142;font-size:14px\"\n" +
                "                                      href=\"\"></a><a target=\"_blank\"\n" +
                "                                      style=\"mso-line-height-rule:exactly;text-decoration:none;color:#2D3142;font-size:14px\"\n" +
                "                                      href=\"\">Privacy Policy</a><a target=\"_blank\"\n" +
                "                                      style=\"mso-line-height-rule:exactly;text-decoration:none;color:#2D3142;font-size:13px\"\n" +
                "                                      href=\"\"></a> • <a target=\"_blank\"\n" +
                "                                      style=\"mso-line-height-rule:exactly;text-decoration:none;color:#2D3142;font-size:14px\"\n" +
                "                                      href=\"\">Unsubscribe</a></p>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                              <tr>\n" +
                "                                <td align=\"center\" style=\"padding:0;Margin:0;padding-top:20px\">\n" +
                "                                  <p\n" +
                "                                    style=\"Margin:0;mso-line-height-rule:exactly;font-family:Imprima, Arial, sans-serif;line-height:21px;letter-spacing:0;color:#2D3142;font-size:14px\">\n" +
                "                                    Copyright © 2023 E-Learning Team</p>\n" +
                "                                </td>\n" +
                "                              </tr>\n" +
                "                            </table>\n" +
                "                          </td>\n" +
                "                        </tr>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </table>\n" +
                "              </td>\n" +
                "            </tr>\n" +
                "          </table>\n" +
                "        </td>\n" +
                "      </tr>\n" +
                "    </table>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
