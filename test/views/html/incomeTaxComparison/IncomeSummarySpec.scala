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

package views.html.incomeTaxComparison

import play.twirl.api.Html
import uk.gov.hmrc.tai.util.viewHelpers.TaiViewSpec
import uk.gov.hmrc.tai.viewModels.IncomeSourceViewModel
import uk.gov.hmrc.time.TaxYearResolver

class IncomeSummarySpec extends TaiViewSpec {

  "Cy plus one income summary view" must {

    "have income from employment header" in {
      doc(view) must haveH2HeadingWithText(messages("tai.incomeTaxComparison.incomeTax.subHeading.incomeFromEmployment"))
    }

    "display one employment income summary table" in {
      doc must haveThWithText(messages("tai.CurrentTaxYearEnds",TaxYearResolver.endOfCurrentTaxYear.toString("d MMMM")))
      doc must haveThWithText(messages("tai.NextTaxYearFrom",TaxYearResolver.startOfNextTaxYear.toString("d MMMM YYYY")))

      doc must haveTdWithText("Company1")
      doc must haveTdWithText("£15,000")
    }

    "display multiple employments income summary table" in{

      doc must haveThWithText(messages("tai.CurrentTaxYearEnds",TaxYearResolver.endOfCurrentTaxYear.toString("d MMMM")))
      doc must haveThWithText(messages("tai.NextTaxYearFrom",TaxYearResolver.startOfNextTaxYear.toString("d MMMM YYYY")))

      doc must haveTdWithText("Company1")
      doc must haveTdWithText("£15,000")

      doc must haveTdWithText("Company2")
      doc must haveTdWithText("£20,000")

    }

  }

  private lazy val incomeSourceViewModelList =
    Seq(IncomeSourceViewModel("Company1", "£15,000", "1150L", true, "123456", true, "", false, "view income details", "fake/url"),
        IncomeSourceViewModel("Company2", "£20,000", "1150L", true, "123456", true, "", false, "view income details", "fake/url"))

  override def view: Html = views.html.incomeTaxComparison.IncomeSummary(incomeSourceViewModelList)
}
