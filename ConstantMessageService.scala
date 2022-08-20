class ConstantMessageServiceForBlog @Inject()(
    sender: LineMessageSender
) {

  def send(receive: ReceiveMessage): Future[Unit] =
    sender.send(SendMessage(List(receive.result.head.from), "なにいってだこいつ"))

}