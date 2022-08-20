class MessageSendApiClient @Inject()(
    ws: WSClient
) extends LineMessageSender {

  import SendMessageConverter._

  // -- ①
  val Url = "https://trialbot-api.line.me/v1/events"
  val EventType = "138311608800106203"
  val ToChannel = 1383378250


  // -- ②
  val proxyUrl: URL = new URL(System.getenv("FIXIE_URL"))
  val host = proxyUrl.getHost
  val port = proxyUrl.getPort
  val protocol = proxyUrl.getProtocol
  val userInfo = proxyUrl.getUserInfo
  val user = userInfo.substring(0, userInfo.indexOf(':'))
  val password = userInfo.substring(userInfo.indexOf(':') + 1)
  val encodedAuth = new BASE64Encoder().encode(userInfo.getBytes())
  val Proxy = DefaultWSProxyServer(host, port, Some(protocol), Some(user), Some(password))

  // -- ③
  val XLineChannelID = System.getenv("X_LINE_CHANNEL_ID")
  val XLineChannelSecret = System.getenv("X_LINE_CHANNEL_SECRET")
  val XLineTrustedUserWithACL = System.getenv("X_LINE_TRUSTED_USER_WITH_ACL")


  override def send(message: SendMessage): Future[Unit] = {
    val request = ws.url(Url)
        .withProxyServer(Proxy)
        .withHeaders(
          "Content-Type" -> "application/json; charset=UTF-8",
          "X-Line-ChannelID" -> XLineChannelID,
          "X-Line-ChannelSecret" -> XLineChannelSecret,
          "X-Line-Trusted-User-With-ACL" -> XLineTrustedUserWithACL,
          "Proxy-Authorization" -> s"Basic $encodedAuth"
        )
    val body = Json.toJson(toSendMessageJsonObject(message))
    request.post(body).map(_ => ())
  }

  // -- ④
  private def toSendMessageJsonObject(sendMessage: SendMessage): SendMessageJsonObject =
  SendMessageJsonObject(
    to = sendMessage.toList,
    toChannel = ToChannel,
    eventType = EventType,
    Content(
      contentType = 1,
      toType = 1,
      text = sendMessage.message
    )
  )
}