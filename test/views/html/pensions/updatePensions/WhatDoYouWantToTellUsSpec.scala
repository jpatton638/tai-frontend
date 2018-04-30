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

package views.html.pensions.updatePensions

import controllers.routes
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.tai.forms.employments.UpdateEmploymentDetailsForm
import uk.gov.hmrc.tai.forms.pensions.WhatDoYouWantToTellUsForm
import uk.gov.hmrc.tai.util.viewHelpers.TaiViewSpec
import uk.gov.hmrc.tai.viewModels.employments.EmploymentViewModel

class WhatDoYouWantToTellUsSpec extends TaiViewSpec {

  private val pensionName = "testPension"

  override def view: Html = views.html.pensions.update.whatDoYouWantToTellUs(pensionName,WhatDoYouWantToTellUsForm.form)

  "whatDoYouWantToTellUs" must {
    behave like pageWithTitle(Messages("tai.updatePension.whatDoYouWantToTellUs.title", pensionName))
    behave like pageWithCombinedHeader(Messages("tai.updatePension.whatDoYouWantToTellUs.preHeading"),
      Messages("tai.updatePension.whatDoYouWantToTellUs.heading",pensionName))
     behave like pageWithContinueButtonForm("/check-income-tax/incorrect-pension/whatDoYouWantToTellUs")
    behave like pageWithCancelLink(routes.IncomeSourceSummaryController.onPageLoad(1))
    behave like pageWithBackLink

    "display a text area to collect further information" in {
      doc must haveElementAtPathWithAttribute("textarea", "name", "pensionDetails")
      doc must haveElementAtPathWithAttribute("textarea", "maxlength", "500")
    }

  }
}