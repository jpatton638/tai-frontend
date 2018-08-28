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

package uk.gov.hmrc.tai.model.domain

class NoMatchPossibleException extends Exception

case class TaxCodePair(previous: Option[TaxCodeRecord], current: Option[TaxCodeRecord])
case class TaxCodePairs(pairs: Seq[TaxCodePair])

object TaxCodePairs {
  def apply(previous: Seq[TaxCodeRecord], current: Seq[TaxCodeRecord]): TaxCodePairs = {
    if (indistinguishableRecords(previous) || indistinguishableRecords(current))  {
      throw new NoMatchPossibleException
    } else {
      TaxCodePairs(
        primaryPairs(previous, current) ++
          secondaryPairs(previous, current) ++
          unMatchedPreviousCodes(previous, current) ++
          unMatchedCurrentCodes(previous, current)
      )
    }
  }

  private def indistinguishableRecords(taxRecords: Seq[TaxCodeRecord]): Boolean = {
    taxRecords
      .filter(!_.primary)
      .groupBy(_.employerName)
      .values
      .map(_.groupBy(_.payrollNumber).values)
      .exists(_.exists(_.length >= 2))
  }

  private def primaryPairs(previous: Seq[TaxCodeRecord], current: Seq[TaxCodeRecord]): Seq[TaxCodePair] = {
    matchedTaxCodes(previous, current).filter(taxCodeRecordPair => taxCodeRecordPair.current.exists(_.primary))
  }

  private def secondaryPairs(previous: Seq[TaxCodeRecord], current: Seq[TaxCodeRecord]): Seq[TaxCodePair] = {
    matchedTaxCodes(previous, current).filterNot(taxCodeRecordPair => taxCodeRecordPair.current.exists(_.primary))
  }

  private def unMatchedCurrentCodes(previous: Seq[TaxCodeRecord], current: Seq[TaxCodeRecord]): Seq[TaxCodePair] = {
    val unpairedRecords = current.filterNot(record => matchedTaxCodes(previous, current).map(_.current).contains(Some(record)))

    unpairedRecords.map(record => TaxCodePair(None, Some(record)))
  }

  private def unMatchedPreviousCodes(previous: Seq[TaxCodeRecord], current: Seq[TaxCodeRecord]): Seq[TaxCodePair] = {
    val unpairedRecords = previous.filterNot(record => matchedTaxCodes(previous, current).map(_.previous).contains(Some(record)))

    unpairedRecords.map(record => TaxCodePair(Some(record), None))
  }

  private def matchedTaxCodes(previous: Seq[TaxCodeRecord], current: Seq[TaxCodeRecord]): Seq[TaxCodePair] = {
    for {
      p <- previous
      c <- current
      if isMatchingPair(p, c)
    } yield TaxCodePair(Some(p), Some(c))
  }

  private def isMatchingPair(record1: TaxCodeRecord, record2: TaxCodeRecord): Boolean = {
    def equivalentPair(r1: TaxCodeRecord, r2: TaxCodeRecord): Boolean = {
      r1.primary == r2.primary && r1.payrollNumber == r2.payrollNumber && r1.employerName == r2.employerName
    }

    (record1.primary && record2.primary) || equivalentPair(record1, record2)
  }
}