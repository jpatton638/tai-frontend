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

package uk.gov.hmrc.tai.viewModels.estimatedIncomeTax

import uk.gov.hmrc.tai.model.domain.{PersonalAllowanceAgedPAA, PersonalAllowanceElderlyPAE, PersonalAllowancePA}
import uk.gov.hmrc.tai.model.domain.calculation.CodingComponent
import uk.gov.hmrc.tai.model.domain.income.TaxCodeIncome
import uk.gov.hmrc.tai.model.domain.tax.TaxBand
import uk.gov.hmrc.tai.viewModels.EstimatedIncomeTaxViewModel.{PersonalSavingsRate, StarterSavingsRate, TaxFreeAllowanceBand}
import uk.gov.hmrc.tai.viewModels.estimatedIncomeTax.ComplexEstimatedIncomeTaxViewModel.{ScottishTaxRegion, UkTaxRegion}

import scala.math.BigDecimal

trait EstimatedIncomeTaxHelper {

  def createPABand(taxFreeAllowance: BigDecimal) = {
    TaxBand(TaxFreeAllowanceBand, "", taxFreeAllowance, 0, Some(0), None, 0)
  }

  def personalAllowanceAmount(codingComponents: Seq[CodingComponent]) = {
    codingComponents.find { component =>
      component.componentType match {
        case compType if compType == PersonalAllowancePA || compType == PersonalAllowanceAgedPAA || compType == PersonalAllowanceElderlyPAE => true
        case _ => false
      }
    }.map(_.amount)
  }

  def hasIncome(taxCodeIncomes: Seq[TaxCodeIncome]) = taxCodeIncomes.nonEmpty


  def retrieveTaxBands(taxBands: List[TaxBand]): List[TaxBand] = {
    val mergedPsaBands = mergeAllowanceTaxBands(taxBands, PersonalSavingsRate)
    val mergedSrBands = mergeAllowanceTaxBands(mergedPsaBands, StarterSavingsRate)
    val bands = mergeAllowanceTaxBands(mergedSrBands, TaxFreeAllowanceBand)
    bands.filter(_.income > 0).sortBy(_.rate)
  }

  def mergeAllowanceTaxBands(taxBands: List[TaxBand], bandType: String) = {
    val (bands, remBands) = taxBands.partition(_.bandType == bandType)
    bands match {
      case Nil => remBands
      case _ => TaxBand(bands.map(_.bandType).head,
        bands.map(_.code).head,
        bands.map(_.income).sum,
        bands.map(_.tax).sum,
        bands.map(_.lowerBand).head,
        bands.map(_.upperBand).head,
        bands.map(_.rate).head) :: remBands
    }
  }

  def findTaxRegion(taxCodeIncomes: Seq[TaxCodeIncome]): String = {
    if(taxCodeIncomes.exists(_.taxCode.startsWith("S"))) ScottishTaxRegion else UkTaxRegion
  }
}
