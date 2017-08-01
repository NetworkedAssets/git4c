Java code:
```java
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

Parser parser = Parser.builder().build();
Node document = parser.parse("This is *Sparta*");
HtmlRenderer renderer = HtmlRenderer.builder().build();
renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
```

Scala code:
```scala
def save = Action { request: Request[AnyContent] =>
  val body: AnyContent = request.body
  val jsonBody: Option[JsValue] = body.asJson

  // Expecting json body
  jsonBody.map { json =>
    Ok("Got: " + (json \ "name").as[String])
  }.getOrElse {
    BadRequest("Expecting application/json request body")
  }
}
```