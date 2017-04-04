package com.sksamuel.elastic4s.mappings

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.mappings.FieldType.{KeywordType, TextType}
import com.sksamuel.elastic4s.testkit.{DualClient, DualElasticSugar}
import org.scalatest.Matchers
import org.scalatest.FlatSpec
import com.sksamuel.elastic4s.analyzers._

class PutMappingApiTest extends FlatSpec with Matchers with ElasticDsl with DualElasticSugar with DualClient {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
  import com.sksamuel.elastic4s.testkit.ResponseConverterImplicits._

  override def beforeRunTests() = execute {
    createIndex("index")
  }.await

  "a put mapping dsl" should "be accepted by the client" in {
    execute {
      putMapping("index" / "type").as(
        geopointField("name"),
        dateField("content") nullValue "no content"
      )
    }.await
  }

  it should "accept same fields as mapping api" in {
    execute {
      putMapping("index" / "type").as(
        dateField("content") nullValue "no content"
      ) dynamic DynamicMapping.False numericDetection true boostNullValue 12.2
    }.await
  }

  it should "accept same several new fields with different types as mapping api" in {
    execute {
      putMapping("index" / "type").as(
        dateField("content") nullValue "no content",
        textField("description") boost 1.5 ,
        doubleField("price"),
        nestedField("children") fields(
          textField("name"),
          dateField("date") nullValue "no date"
        )
      ) dynamic DynamicMapping.False numericDetection true boostNullValue 12.2
    }.await
  }
}
