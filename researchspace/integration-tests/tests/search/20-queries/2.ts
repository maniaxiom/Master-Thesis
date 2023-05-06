/**
 * ResearchSpace
 * Copyright (C) 2015-2020, © Trustees of the British Museum
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import { WebDriver } from 'selenium-webdriver';
import { Test } from 'tap';
import { Options } from '../../options';
import { testSearch } from '../util';
import { sameSets } from '../../util';

/**
 * @author Artem Kozlov <ak@metaphacts.com>
 */
export async function test_2(driver: WebDriver, test: Test, options: Options) {
  await test.test(
    '2) Find: Actors is owner of Thing where things created at Africa',
    async (t: Test) => await testSearch(driver, t, options, async (s, d) => {
      await s.setDomain('Actor');
      await s.setRange('Thing');
      await s.setRelation('is owner of');
      await s.startNestedSearch();
      await s.setRange('Place');
      await s.setRelation('created at');
      await s.typeRange('Africa');
      await s.toggleRangeNode('Africa');
      await s.submitRangeTree();

      const expectedResult = 2191;
      const resultNumber = await s.getNumResults();
      await t.equal(expectedResult, resultNumber, 'correct number of intermediate results');

      await s.showFacet();
      const rel = await s.openFacetRelation('is member of');
      await s.clickFacetCheckbox(rel, 'Factory of Garrard, Robert');

      const results = await s.getSearchResults();
      await sameSets(t, results, [
        '<http://collection.britishmuseum.org/id/person-institution/70138>'
      ]);
    }));
}
