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

package uk.gov.hmrc.tai.service

import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.LocalDate
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.tai.connectors.TaxCodeChangeConnector
import uk.gov.hmrc.tai.connectors.responses.{TaiResponse, TaiSuccessResponseWithPayload}
import uk.gov.hmrc.tai.model.domain.TaxCodeHistory

import scala.concurrent.Future

trait TaxCodeChangeService {

  def taxCodeChangeConnector: TaxCodeChangeConnector

  def taxCodeHistory(nino: Nino)(implicit hc: HeaderCarrier): Future[TaiResponse] = {
    taxCodeChangeConnector.taxCodeHistory(nino)
  }

  def latestTaxCodeChangeDate(nino: Nino)(implicit hc: HeaderCarrier): Future[LocalDate] = {
    taxCodeHistory(nino) map {
      case TaiSuccessResponseWithPayload(taxCodeHistory: TaxCodeHistory) => new LocalDate(taxCodeHistory.mostRecentTaxCodeChangeDate)
      case _ => throw new RuntimeException("Could not fetch tax code history")
    }
  }


}

object TaxCodeChangeService extends TaxCodeChangeService {
  override val taxCodeChangeConnector: TaxCodeChangeConnector = TaxCodeChangeConnector
}
