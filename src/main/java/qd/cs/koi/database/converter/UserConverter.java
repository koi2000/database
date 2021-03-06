/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package qd.cs.koi.database.converter;

import qd.cs.koi.database.entity.UserDO;
import qd.cs.koi.database.interfaces.User.UserDTO;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserConverter extends BaseUserConverter<UserDO, UserDTO> {
}