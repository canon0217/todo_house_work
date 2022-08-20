trait LineMessageSender {

  def send(message: SendMessage): Future[Unit]

}