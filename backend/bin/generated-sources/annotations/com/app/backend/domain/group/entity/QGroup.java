package com.app.backend.domain.group.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGroup is a Querydsl query type for Group
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGroup extends EntityPathBase<Group> {

    private static final long serialVersionUID = -1108800775L;

    public static final QGroup group = new QGroup("group1");

    public final com.app.backend.global.entity.QBaseEntity _super = new com.app.backend.global.entity.QBaseEntity(this);

    public final StringPath city = createString("city");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    //inherited
    public final BooleanPath disabled = _super.disabled;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> maxRecruitCount = createNumber("maxRecruitCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final StringPath province = createString("province");

    public final EnumPath<RecruitStatus> recruitStatus = createEnum("recruitStatus", RecruitStatus.class);

    public final StringPath town = createString("town");

    public QGroup(String variable) {
        super(Group.class, forVariable(variable));
    }

    public QGroup(Path<? extends Group> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGroup(PathMetadata metadata) {
        super(Group.class, metadata);
    }

}

