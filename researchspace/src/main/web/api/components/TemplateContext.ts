/**
 * ResearchSpace
 * Copyright (C) 2020, © Trustees of the British Museum
 * Copyright (C) 2015-2019, metaphacts GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import * as PropTypes from 'prop-types';

import * as TemplateService from 'platform/api/services/template';

/**
 * @author Alexey Morozov
 */
export interface TemplateContext {
  readonly templateScope: TemplateService.TemplateScope;
  readonly templateDataContext?: TemplateService.CapturedContext;
}

export const TemplateContextTypes: Readonly<Record<keyof TemplateContext, any>> = {
  templateScope: PropTypes.object,
  templateDataContext: PropTypes.object,
};
