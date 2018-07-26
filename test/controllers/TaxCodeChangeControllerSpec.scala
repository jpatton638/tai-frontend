/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import builders.{AuthBuilder, RequestBuilder}
import mocks.MockTemplateRenderer
import org.mockito.Matchers.{any, eq => mockEq}
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.test.Helpers.{status, _}
import uk.gov.hmrc.domain.{Generator, Nino}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.frontend.auth.connectors.domain.Authority
import uk.gov.hmrc.play.frontend.auth.connectors.{AuthConnector, DelegationConnector}
import uk.gov.hmrc.play.partials.FormPartialRetriever
import uk.gov.hmrc.renderer.TemplateRenderer
import uk.gov.hmrc.tai.service.PersonService

import scala.concurrent.Future
import scala.util.Random


class TaxCodeChangeControllerSpec extends PlaySpec
  with MockitoSugar
  with FakeTaiPlayApplication
  with I18nSupport {

  implicit val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

  "whatHappensNext" must {
    "show 'What happens next' page" when {
      "the request has an authorised session" in {
        val SUT = createSUT(true)
        val result = SUT.whatHappensNext()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe OK
      }
    }

    "don't show 'What happens next' page if 'tax code change journey' is toggled off" when {
      "the request has an authorised session" in {
        val SUT = createSUT()
        val result = SUT.whatHappensNext()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe NOT_FOUND
      }
    }
  }

  "yourTaxFreeAmount" must {
    "show 'Your tax-free amount' page" when {
      "the request has an authorised session" in {
        val SUT = createSUT(true)
        val result = SUT.yourTaxFreeAmount()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe OK
      }
    }

    "don't show 'Your tax-free amount' page if 'tax code change journey' is toggled off" when {
      "the request has an authorised session" in {
        val SUT = createSUT()
        val result = SUT.yourTaxFreeAmount()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe NOT_FOUND
      }
    }
  }

  "taxCodeComparison" must {
    "show 'Your tax code comparison' page" when {
      "the request has an authorised session" in {
        val SUT = createSUT(true)
        val result = SUT.taxCodeComparison()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe OK
      }
    }

    "don't show 'Your tax code comparison' page if 'tax code change journey' is toggled off" when {
      "the request has an authorised session" in {
        val SUT = createSUT()
        val result = SUT.taxCodeComparison()(RequestBuilder.buildFakeRequestWithAuth("GET"))
        status(result) mustBe NOT_FOUND
      }
    }
  }


  private def createSUT(taxCodeChangeJourneyEnabled: Boolean = false) = new SUT(taxCodeChangeJourneyEnabled)

  def generateNino: Nino = new Generator(new Random).nextNino

  class SUT(taxCodeChangeJourneyEnabled: Boolean) extends TaxCodeChangeController {

    override implicit val partialRetriever: FormPartialRetriever = mock[FormPartialRetriever]
    override implicit val templateRenderer: TemplateRenderer = MockTemplateRenderer
    override val personService: PersonService = mock[PersonService]
    override protected val delegationConnector: DelegationConnector = mock[DelegationConnector]
    override protected val authConnector: AuthConnector = mock[AuthConnector]
    override val auditConnector: AuditConnector = mock[AuditConnector]
    override val taxCodeChangeEnabled: Boolean = taxCodeChangeJourneyEnabled

    val ad: Future[Some[Authority]] = Future.successful(Some(AuthBuilder.createFakeAuthority(generateNino.toString())))
    when(authConnector.currentAuthority(any(), any())).thenReturn(ad)
    when(personService.personDetails(any())(any())).thenReturn(Future.successful(fakePerson(generateNino)))
  }

}
