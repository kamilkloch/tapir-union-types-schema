## Tapir OpenAPI schema generation for union types

```scala
sealed trait Fruit

object Fruit {
  case class Apple(color: String) extends Fruit

  case class Potato(weight: Double) extends Fruit

  private implicit val config: Configuration = Configuration.default
  implicit val fruitCodec: io.circe.Codec[Fruit] = deriveConfiguredCodec
}
```


Generated OpenApi YAML is incorrect - it is missing the discriminant level. An Apple (from Fruit trait perspective) is of shape
```json
{ 
  "Apple" : { 
    "color": "string"
  }
}
```
, and not
```json
{ 
  "color": "string"
}
```

Generated OpenApi YAML:
```yaml
openapi: 3.1.0
info:
  title: linguistic-nightingale
  version: 1.0.0
paths:
  /fruit:
    post:
      operationId: postFruit
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/Fruit'
        required: true
      responses:
        '200':
          description: ''
        '400':
          description: 'Invalid value for: body'
          content:
            text/plain:
              schema:
                type: string
components:
  schemas:
    Apple:
      required:
      - color
      type: object
      properties:
        color:
          type: string
    Fruit:
      oneOf:
      - $ref: '#/components/schemas/Apple'
      - $ref: '#/components/schemas/Potato'
    Potato:
      required:
      - weight
      type: object
      properties:
        weight:
          type: number
          format: double
```