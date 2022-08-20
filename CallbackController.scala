class CallbackControllerForBlog @Inject()(
    constantService: ConstantMessageService // -- ①
) extends Controller {

  import domains.line.LineConverters._

  def callback = Action.async(parse.json) { req =>
    for {
      received <- FutureSupport.jsResultToFuture(req.body.validate[ReceiveMessage]) // -- ②
      constantService.send(received)
    } yield NoContent
  }
}