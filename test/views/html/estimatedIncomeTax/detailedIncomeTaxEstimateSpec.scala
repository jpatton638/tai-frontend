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

package views.html.estimatedIncomeTax

import controllers.routes
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.play.language.LanguageUtils.Dates
import uk.gov.hmrc.tai.model.domain._
import uk.gov.hmrc.tai.model.domain.calculation.CodingComponent
import uk.gov.hmrc.tai.model.domain.income._
import uk.gov.hmrc.tai.model.domain.tax.{IncomeCategory, TaxBand, TotalTax}
import uk.gov.hmrc.tai.util.BandTypesConstants
import uk.gov.hmrc.tai.util.viewHelpers.TaiViewSpec
import uk.gov.hmrc.tai.viewModels.{HelpLink, Label, TaxExplanationViewModel}
import uk.gov.hmrc.tai.viewModels.estimatedIncomeTax.{AdditionalTaxDetailRow, DetailedIncomeTaxEstimateViewModel, ReductionTaxRow, SimpleEstimatedIncomeTaxViewModel}
import uk.gov.hmrc.time.TaxYearResolver
import uk.gov.hmrc.urls.Link

class detailedIncomeTaxEstimateSpec extends TaiViewSpec with BandTypesConstants {

  "view" must {

    behave like pageWithTitle(messages("tai.estimatedIncome.detailedEstimate.title"))
    behave like pageWithHeader(messages("tai.estimatedIncome.detailedEstimate.heading"))
    behave like pageWithBackLink


    "show correct header content" in {

      val expectedTaxYearString = Messages("tai.taxYear",
        nonBreakable(Dates.formatDate(TaxYearResolver.startOfCurrentTaxYear)),
        nonBreakable(Dates.formatDate(TaxYearResolver.endOfCurrentTaxYear)))

      val accessiblePreHeading = doc.select("""header span[class="visuallyhidden"]""")
      accessiblePreHeading.text mustBe Messages("tai.estimatedIncome.accessiblePreHeading")

      val preHeading = doc.select("header p")
      preHeading.text mustBe s"${Messages("tai.estimatedIncome.accessiblePreHeading")} $expectedTaxYearString"
    }

    "have a heading for the Total Income Tax Estimate" in {
      doc(view) must haveH2HeadingWithText(messages("tai.incomeTax.totalIncomeTaxEstimate") + " £18,573")
    }


    "paragraph with additional Income Tax payable not being included in estimate" should {
      "be shown when text is provided" in {
        val vm = defaultViewModel.copy(selfAssessmentAndPayeText = Some("Stub addition Income Payable Text"))

        doc(view(vm)) must haveParagraphWithText("Stub addition Income Payable Text")
      }
    }

    "heading and text for non savings income section displays" should {
      "be 'Tax on your employment income' when income is only from employment" in {
        val totalTax = TotalTax(0,Seq.empty[IncomeCategory],None, None, None, None, None)
        val taxCodeIncome: Seq[TaxCodeIncome] = List(TaxCodeIncome(EmploymentIncome, None, 0, "", "", "", OtherBasisOperation, Live))
        val taxAccountSummary = TaxAccountSummary(0,0,0,0,0)
        val nonTaxCodeIncome = NonTaxCodeIncome(None, Seq.empty[OtherNonTaxCodeIncome])
        val viewModel = DetailedIncomeTaxEstimateViewModel(totalTax, taxCodeIncome, taxAccountSummary, Seq.empty[CodingComponent], nonTaxCodeIncome)
        val document = doc(view(viewModel))
        val message = Messages("your.total.income.from.employment.desc",
                              "£0",
                              "tax-free amount","£0")

        document must haveH2HeadingWithText(messages("tax.on.your.employment.income"))
        document must haveParagraphWithText(message)
      }

      "be 'Tax on your private pension income' when income is only from pension" in {
        val totalTax = TotalTax(0,Seq.empty[IncomeCategory],None, None, None, None, None)
        val taxCodeIncome: Seq[TaxCodeIncome] = List(TaxCodeIncome(PensionIncome, None, 0, "", "", "", OtherBasisOperation, Live))
        val taxAccountSummary = TaxAccountSummary(0,0,0,0,0)
        val nonTaxCodeIncome = NonTaxCodeIncome(None, Seq.empty[OtherNonTaxCodeIncome])
        val viewModel = DetailedIncomeTaxEstimateViewModel(totalTax, taxCodeIncome, taxAccountSummary, Seq.empty[CodingComponent], nonTaxCodeIncome)
        val document = doc(view(viewModel))
        val message = Messages("your.total.income.from.private.pension.desc",
                              "£0",
                              "tax-free amount","£0")

        document must haveH2HeadingWithText(messages("tax.on.your.private.pension.income"))
        document must haveParagraphWithText(message)
      }

      "be 'Tax on your PAYE income' when income is only from any other combination" when {
        "Employment and pension income" in {
          val totalTax = TotalTax(0, Seq.empty[IncomeCategory], None, None, None, None, None)
          val taxCodeIncome: Seq[TaxCodeIncome] = List(TaxCodeIncome(PensionIncome, None, 0, "", "", "", OtherBasisOperation, Live), TaxCodeIncome(EmploymentIncome, None, 0, "", "", "", OtherBasisOperation, Live))
          val taxAccountSummary = TaxAccountSummary(0, 0, 0, 0, 0)
          val nonTaxCodeIncome = NonTaxCodeIncome(None, Seq.empty[OtherNonTaxCodeIncome])
          val viewModel = DetailedIncomeTaxEstimateViewModel(totalTax, taxCodeIncome, taxAccountSummary, Seq.empty[CodingComponent], nonTaxCodeIncome)
          val document = doc(view(viewModel))
          val message = Messages("your.total.income.from.paye.desc",
                                "£0",
                                "tax-free amount","£0")

          document must haveH2HeadingWithText(messages("tax.on.your.paye.income"))
          document must haveParagraphWithText(message)
        }

        "JSA income" in {
          val totalTax = TotalTax(0, Seq.empty[IncomeCategory], None, None, None, None, None)
          val taxCodeIncome: Seq[TaxCodeIncome] = List(TaxCodeIncome(JobSeekerAllowanceIncome, None, 0, "", "", "", OtherBasisOperation, Live))
          val taxAccountSummary = TaxAccountSummary(0, 0, 0, 0, 0)
          val nonTaxCodeIncome = NonTaxCodeIncome(None, Seq.empty[OtherNonTaxCodeIncome])
          val viewModel = DetailedIncomeTaxEstimateViewModel(totalTax, taxCodeIncome, taxAccountSummary, Seq.empty[CodingComponent], nonTaxCodeIncome)
          val document = doc(view(viewModel))
          val message = Messages("your.total.income.from.paye.desc",
                                "£0",
                                "tax-free amount","£0","PAYE")

          document must haveH2HeadingWithText(messages("tax.on.your.paye.income"))
          document must haveParagraphWithText(message)
        }

        "Other income" in {
          val totalTax = TotalTax(0, Seq.empty[IncomeCategory], None, None, None, None, None)
          val taxCodeIncome: Seq[TaxCodeIncome] = List(TaxCodeIncome(OtherIncome, None, 0, "", "", "", OtherBasisOperation, Live))
          val taxAccountSummary = TaxAccountSummary(0, 0, 0, 0, 0)
          val nonTaxCodeIncome = NonTaxCodeIncome(None, Seq.empty[OtherNonTaxCodeIncome])
          val viewModel = DetailedIncomeTaxEstimateViewModel(totalTax, taxCodeIncome, taxAccountSummary, Seq.empty[CodingComponent], nonTaxCodeIncome)
          val document = doc(view(viewModel))
          val message = Messages("your.total.income.from.paye.desc",
                                "£0",
                                "tax-free amount","£0","PAYE")

          document must haveH2HeadingWithText(messages("tax.on.your.paye.income"))
          document must haveParagraphWithText(message)
        }
      }
    }

    "display table body" when {
      "UK user have non-savings" in {
        val nonSavings = List(
          TaxBand("B", "", 32010, 6402, None, None, 20),
          TaxBand("D0", "", 36466, 36466, None, None, 40)
        )
        val viewWithNonSavings: Html = views.html.howIncomeTaxIsCalculated(TaxExplanationViewModel(nonSavings, Seq.empty[TaxBand], Seq.empty[TaxBand], UkBands))
        doc(viewWithNonSavings) must haveTdWithText("32,010")
        doc(viewWithNonSavings) must haveTdWithText(messages("uk.bandtype.B"))
        doc(viewWithNonSavings) must haveTdWithText("20%")
        doc(viewWithNonSavings) must haveTdWithText("6,402.00")
        doc(viewWithNonSavings) must haveTdWithText("36,466")
        doc(viewWithNonSavings) must haveTdWithText(messages("uk.bandtype.D0"))
        doc(viewWithNonSavings) must haveTdWithText("40%")
        doc(viewWithNonSavings) must haveTdWithText("36,466.00")
      }

      "Scottish user have non-savings" in {
        val nonSavings = List(
          TaxBand("B", "", 32010, 6402, None, None, 20),
          TaxBand("D0", "", 36466, 36466, None, None, 40)
        )
        val viewWithNonSavings: Html = views.html.howIncomeTaxIsCalculated(TaxExplanationViewModel(nonSavings, Seq.empty[TaxBand], Seq.empty[TaxBand], ScottishBands))
        doc(viewWithNonSavings) must haveTdWithText("32,010")
        doc(viewWithNonSavings) must haveTdWithText(messages("scottish.bandtype.B"))
        doc(viewWithNonSavings) must haveTdWithText("20%")
        doc(viewWithNonSavings) must haveTdWithText("6,402.00")
        doc(viewWithNonSavings) must haveTdWithText("36,466")
        doc(viewWithNonSavings) must haveTdWithText(messages("scottish.bandtype.D0"))
        doc(viewWithNonSavings) must haveTdWithText("40%")
        doc(viewWithNonSavings) must haveTdWithText("36,466.00")
      }

      "UK user have savings" in {
        val savings = List(
          TaxBand("LSR", "", 32010, 6402, None, None, 20),
          TaxBand("HSR1", "", 36466, 36466, None, None, 40)
        )
        val viewWithSavings: Html = views.html.howIncomeTaxIsCalculated(TaxExplanationViewModel(Seq.empty[TaxBand], savings, Seq.empty[TaxBand], UkBands))
        doc(viewWithSavings) must haveTdWithText("32,010")
        doc(viewWithSavings) must haveTdWithText(messages("uk.bandtype.LSR"))
        doc(viewWithSavings) must haveTdWithText("20%")
        doc(viewWithSavings) must haveTdWithText("6,402.00")
        doc(viewWithSavings) must haveTdWithText("36,466")
        doc(viewWithSavings) must haveTdWithText(messages("uk.bandtype.HSR1"))
        doc(viewWithSavings) must haveTdWithText("40%")
        doc(viewWithSavings) must haveTdWithText("36,466.00")
      }

      "Scottish user have savings" in {
        val savings = List(
          TaxBand("LSR", "", 32010, 6402, None, None, 20),
          TaxBand("HSR1", "", 36466, 36466, None, None, 40)
        )
        val viewWithSavings: Html = views.html.howIncomeTaxIsCalculated(TaxExplanationViewModel(Seq.empty[TaxBand], savings, Seq.empty[TaxBand], ScottishBands))
        doc(viewWithSavings) must haveTdWithText("32,010")
        doc(viewWithSavings) must haveTdWithText(messages("scottish.bandtype.LSR"))
        doc(viewWithSavings) must haveTdWithText("20%")
        doc(viewWithSavings) must haveTdWithText("6,402.00")
        doc(viewWithSavings) must haveTdWithText("36,466")
        doc(viewWithSavings) must haveTdWithText(messages("scottish.bandtype.HSR1"))
        doc(viewWithSavings) must haveTdWithText("40%")
        doc(viewWithSavings) must haveTdWithText("36,466.00")
      }

      "UK user have dividends" in {
        val dividends = List(
          TaxBand("LDR", "", 32010, 6402, None, None, 20),
          TaxBand("HDR1", "", 36466, 36466, None, None, 40)
        )
        val viewWithDividends: Html = views.html.howIncomeTaxIsCalculated(TaxExplanationViewModel(Seq.empty[TaxBand], Seq.empty[TaxBand], dividends, UkBands))
        doc(viewWithDividends) must haveTdWithText("32,010")
        doc(viewWithDividends) must haveTdWithText(messages("uk.bandtype.LDR"))
        doc(viewWithDividends) must haveTdWithText("20%")
        doc(viewWithDividends) must haveTdWithText("6,402.00")
        doc(viewWithDividends) must haveTdWithText("36,466")
        doc(viewWithDividends) must haveTdWithText(messages("uk.bandtype.HDR1"))
        doc(viewWithDividends) must haveTdWithText("40%")
        doc(viewWithDividends) must haveTdWithText("36,466.00")
      }

      "scottish user have dividends" in {
        val dividends = List(
          TaxBand("LDR", "", 32010, 6402, None, None, 20),
          TaxBand("HDR1", "", 36466, 36466, None, None, 40)
        )
        val viewWithDividends: Html = views.html.howIncomeTaxIsCalculated(TaxExplanationViewModel(Seq.empty[TaxBand], Seq.empty[TaxBand], dividends, UkBands))
        doc(viewWithDividends) must haveTdWithText("32,010")
        doc(viewWithDividends) must haveTdWithText(messages("scottish.bandtype.LDR"))
        doc(viewWithDividends) must haveTdWithText("20%")
        doc(viewWithDividends) must haveTdWithText("6,402.00")
        doc(viewWithDividends) must haveTdWithText("36,466")
        doc(viewWithDividends) must haveTdWithText(messages("scottish.bandtype.HDR1"))
        doc(viewWithDividends) must haveTdWithText("40%")
        doc(viewWithDividends) must haveTdWithText("36,466.00")
      }

    }

    "have tax on your employment income section" in {

      doc(view) must haveH2HeadingWithText(messages("tax.on.your.employment.income"))
      doc(view) must haveParagraphWithText(Html(messages("your.total.income.from.employment.desc",
        "£68,476",
        messages("tai.estimatedIncome.taxFree.link"),
        "£11,500")).body)
    }

    "have tax on your dividend income section" when{

      "dividends income exists " in {

        doc(view) must haveH2HeadingWithText(messages("tai.estimatedIncome.detailedEstimate.dividendIncome.subHeading"))
        doc(view) must haveParagraphWithText(messages("tai.estimatedIncome.dividend.para.desc","20,000","5,000"))

      }
    }

    "have tax on your savings income section" when{

      "dividends income exists " in {

        doc(view) must haveH2HeadingWithText(messages("tai.estimatedIncome.detailedEstimate.dividendIncome.subHeading"))
        doc(view) must haveParagraphWithText(messages("tai.estimatedIncome.dividend.para.desc","20,000","5,000"))

      }
    }


  }

  "have additional tax table" in {

    val additionalRows = Seq(
      AdditionalTaxDetailRow(Label(Messages("tai.taxCalc.UnderpaymentPreviousYear.title"),
        Some(HelpLink(Messages("what.does.this.mean"),
          controllers.routes.UnderpaymentFromPreviousYearController.underpaymentExplanation.url.toString,
            "underPaymentFromPreviousYear"))), 100),
      AdditionalTaxDetailRow(Label(Messages("tai.taxcode.deduction.type-45"),
        Some(HelpLink(Messages("what.does.this.mean"),
          controllers.routes.PotentialUnderpaymentController.potentialUnderpaymentPage.url.toString,
            "estimatedTaxOwedLink"))), 50),
      AdditionalTaxDetailRow(Label(Messages("tai.taxCalc.OutstandingDebt.title")), 150),
      AdditionalTaxDetailRow(Label(Messages("tai.taxCalc.childBenefit.title")), 300),
      AdditionalTaxDetailRow(Label(Messages("tai.taxCalc.excessGiftAidTax.title")), 100),
      AdditionalTaxDetailRow(Label(Messages("tai.taxCalc.excessWidowsAndOrphans.title")), 100),
      AdditionalTaxDetailRow(Label(Messages("tai.taxCalc.pensionPaymentsAdjustment.title")), 200)
    )
    val model = createViewModel(additionalRows, Seq.empty[ReductionTaxRow])
    def additionalDetailView: Html = views.html.estimatedIncomeTax.detailedIncomeTaxEstimate(model)

    doc(additionalDetailView).select("#additionalTaxTable").size() mustBe 1
    doc(additionalDetailView).select("#additionalTaxTable-heading").text mustBe Messages("tai.estimatedIncome.additionalTax.title")
    doc(additionalDetailView).select("#additionalTaxTable-desc").text() mustBe Messages("tai.estimatedIncome.additionalTax.desc")
    doc(additionalDetailView).getElementsMatchingOwnText("TaxDescription").hasAttr("data-journey-click") mustBe false
    doc(additionalDetailView) must haveThWithText(messages("tax.adjustments"))
    doc(additionalDetailView) must haveTdWithText(messages("tai.taxCalc.OutstandingDebt.title"))
    doc(additionalDetailView) must haveTdWithText(messages("tai.taxCalc.childBenefit.title"))
    doc(additionalDetailView) must haveTdWithText(messages("tai.taxCalc.excessGiftAidTax.title"))
    doc(additionalDetailView) must haveTdWithText(messages("tai.taxCalc.excessWidowsAndOrphans.title"))
    doc(additionalDetailView) must haveTdWithText(messages("tai.taxCalc.pensionPaymentsAdjustment.title"))
    doc(additionalDetailView) must haveTdWithText(s"${messages("tai.taxCalc.UnderpaymentPreviousYear.title")} ${messages("what.does.this.mean")}")
    doc(additionalDetailView).select("#underPaymentFromPreviousYear").attr("href") mustBe controllers.routes.UnderpaymentFromPreviousYearController.underpaymentExplanation.url.toString
    doc(additionalDetailView) must haveTdWithText(s"${messages("tai.taxcode.deduction.type-45")} ${messages("what.does.this.mean")}")
    doc(additionalDetailView).select("#estimatedTaxOwedLink").attr("href") mustBe controllers.routes.PotentialUnderpaymentController.potentialUnderpaymentPage.url.toString
  }

  "have reduction tax table" in {
    val taxCodeLink = Link.toInternalPage(
      url = routes.YourTaxCodeController.taxCodes().toString,
      value = Some(Messages("tai.taxCollected.atSource.marriageAllowance.description.linkText"))
    ).toHtml.body

    val  reductionTaxRows = Seq(
      ReductionTaxRow(Messages("tai.taxCollected.atSource.otherIncome.description"), 100, Messages("tai.taxCollected.atSource.otherIncome.title")),
      ReductionTaxRow(Messages("tai.taxCollected.atSource.dividends.description", 10), 200, Messages("tai.taxCollected.atSource.dividends.title")),
      ReductionTaxRow(Messages("tai.taxCollected.atSource.bank.description", 20), 100, Messages("tai.taxCollected.atSource.bank.title")),
      ReductionTaxRow(Messages("tai.taxCollected.atSource.marriageAllowance.description", 0, taxCodeLink), 135, Messages("tai.taxCollected.atSource.marriageAllowance.title")),
      ReductionTaxRow(Messages("tai.taxCollected.atSource.maintenancePayments.description"), 200, Messages("tai.taxCollected.atSource.marriageAllowance.title")),
      ReductionTaxRow(Messages("tai.taxCollected.atSource.enterpriseInvestmentSchemeRelief.description"), 100, Messages("tai.taxCollected.atSource.enterpriseInvestmentSchemeRelief.title")),
      ReductionTaxRow(Messages("tai.taxCollected.atSource.concessionalRelief.description"), 600, Messages("tai.taxCollected.atSource.concessionalRelief.title")),
      ReductionTaxRow(Messages("tai.taxCollected.atSource.doubleTaxationRelief.description"), 900, Messages("tai.taxCollected.atSource.doubleTaxationRelief.title")),
      ReductionTaxRow(Messages("gift.aid.tax.relief",0,1000), 1000, Messages("gift.aid.savings")),
      ReductionTaxRow(Messages("personal.pension.payment.relief",0,1100), 1100, Messages("personal.pension.payments"))
    )

    val model = createViewModel(Seq.empty[AdditionalTaxDetailRow], reductionTaxRows)
    def reductionTaxDetailView: Html = views.html.estimatedIncomeTax.detailedIncomeTaxEstimate(model)

    doc(reductionTaxDetailView).select("#taxPaidElsewhereTable").size() mustBe 1
    doc(reductionTaxDetailView).select("#taxPaidElsewhereTable-heading").text() mustBe Messages("tai.estimatedIncome.reductionsTax.title")
    doc(reductionTaxDetailView).select("#taxPaidElsewhereTable-desc").text() mustBe Messages("tai.estimatedIncome.reductionsTax.desc")
    doc(reductionTaxDetailView) must haveTdWithText(s"${messages("tai.taxCollected.atSource.otherIncome.title")} ${messages("tai.taxCollected.atSource.otherIncome.description")}")
    doc(reductionTaxDetailView) must haveTdWithText(s"${messages("tai.taxCollected.atSource.dividends.title")} ${messages("tai.taxCollected.atSource.dividends.description",10)}")
    doc(reductionTaxDetailView) must haveTdWithText(s"${messages("tai.taxCollected.atSource.bank.title")} ${messages("tai.taxCollected.atSource.bank.description", 20)}")
    doc(reductionTaxDetailView) must haveTdWithText(s"${messages("tai.taxCollected.atSource.marriageAllowance.title")} ${messages("tai.taxCollected.atSource.marriageAllowance.description",
      0, Messages("tai.taxCollected.atSource.marriageAllowance.description.linkText"))}")
    doc(reductionTaxDetailView) must haveTdWithText(s"${messages("tai.taxCollected.atSource.marriageAllowance.title")} ${messages("tai.taxCollected.atSource.maintenancePayments.description", 200)}")
    doc(reductionTaxDetailView) must haveLinkWithText(Messages("tai.taxCollected.atSource.marriageAllowance.description.linkText"))
    doc(reductionTaxDetailView) must haveTdWithText(s"${messages("tai.taxCollected.atSource.enterpriseInvestmentSchemeRelief.title")} ${messages("tai.taxCollected.atSource.enterpriseInvestmentSchemeRelief.description", 100)}")
    doc(reductionTaxDetailView) must haveTdWithText(s"${messages("tai.taxCollected.atSource.concessionalRelief.title")} ${messages("tai.taxCollected.atSource.concessionalRelief.description")}")
    doc(reductionTaxDetailView) must haveTdWithText(s"${messages("tai.taxCollected.atSource.doubleTaxationRelief.title")} ${messages("tai.taxCollected.atSource.doubleTaxationRelief.description")}")
    doc(reductionTaxDetailView) must haveTdWithText(s"${messages("gift.aid.savings")} ${messages("gift.aid.tax.relief",0,1000)}")
    doc(reductionTaxDetailView) must haveTdWithText(s"${messages("personal.pension.payments")} ${messages("personal.pension.payment.relief",0,1100)}")
  }

  val ukTaxBands = List(
    TaxBand("pa", "", 11500, 0, None, None, 0),
    TaxBand("B", "", 32010, 6402, None, None, 20),
    TaxBand("D0", "", 36466, 14586.4, None, None, 40))

  val defaultViewModel = DetailedIncomeTaxEstimateViewModel(ukTaxBands, Seq.empty[TaxBand], List.empty[TaxBand], "UK", 18573, 68476,
    11500, Seq.empty[AdditionalTaxDetailRow], Seq.empty[ReductionTaxRow], None, false, None, None, 20000, 5000, None,messages("tax.on.your.employment.income"),
    messages("your.total.income.from.employment.desc",
      "£68,476",
      messages("tai.estimatedIncome.taxFree.link"),
      "£11,500"))

  def view(vm: DetailedIncomeTaxEstimateViewModel = defaultViewModel): Html = views.html.estimatedIncomeTax.detailedIncomeTaxEstimate(vm)
  override def view: Html = view(defaultViewModel)

  def createViewModel(additionalTaxTable: Seq[AdditionalTaxDetailRow], reductionTaxTable: Seq[ReductionTaxRow]) : DetailedIncomeTaxEstimateViewModel = {
    DetailedIncomeTaxEstimateViewModel(
      nonSavings = List.empty[TaxBand],
      savings = Seq.empty[TaxBand],
      dividends = List.empty[TaxBand],
      taxRegion = "uk",
      incomeTaxEstimate = 900,
      incomeEstimate = 16000,
      taxFreeAllowance = 11500,
      additionalTaxTable = additionalTaxTable,
      reductionTaxTable = reductionTaxTable,
      incomeTaxReducedToZeroMessage = None,
      hasPotentialUnderPayment = false,
      ssrValue = None,
      psrValue = None,
      totalDividendIncome = 0,
      taxFreeDividendAllowance = 0,
      selfAssessmentAndPayeText = None,
      taxOnIncomeTypeHeading = "",
      taxOnIncomeTypeDescription = ""
    )
  }

}